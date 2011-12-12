package org.github.eq4j;

import static java.util.Arrays.copyOf;

import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.core.Predicate;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;

public final class Entities {

	private static final NamingPolicy namingPolicy = new NamePolicy();
	private static final Class<?>[] INTERFACES = { Entity.class };
	private static final Map<Class<?>, Factory> factoryCache = new HashMap<Class<?>, Factory>();

	public static <E> E entityOf(final Class<E> entityType) {
		return entityOf(entityType, new EntityProxy());
	}

	static <E> E entityOf(final Class<E> entityType, final EntityProxy proxy) {
		Factory factory = getOrCreateFactory(entityType);
		return entityType.cast(factory.newInstance(proxy));
	}

	private static <E> Factory getOrCreateFactory(final Class<E> entityType) {
		Factory factory = factoryCache.get(entityType);
		if (factory == null) {
			synchronized (factoryCache) {
				factory = factoryCache.get(entityType);
				if (factory == null) {
					factory = createFactory(entityType);
					factoryCache.put(entityType, factory);
				}
			}
		}
		return factory;
	}

	private static <E> Factory createFactory(final Class<E> entityType) {
		Enhancer enhancer = new Enhancer();
		enhancer.setUseCache(false);
		enhancer.setUseFactory(true);
		if (entityType.isInterface()) {
			Class<?>[] interfaces = copyOf(INTERFACES, INTERFACES.length + 1);
			interfaces[INTERFACES.length] = entityType;
			enhancer.setInterfaces(interfaces);
		} else {
			enhancer.setSuperclass(entityType);
			enhancer.setInterfaces(INTERFACES);
		}
		enhancer.setCallback(new EntityProxy());
		enhancer.setInterceptDuringConstruction(false);
		enhancer.setNamingPolicy(namingPolicy);
		return Factory.class.cast(enhancer.create());
	}

	private static final class NamePolicy implements NamingPolicy {
		private int sequence = 1;

		@Override
		public String getClassName(final String prefix, final String source, final Object key, final Predicate names) {
			return packageName() + "." + className(prefix) + "$$EnhancedByCglibForEQ4J$$" + sequence++;
		}

		private String packageName() {
			return Entity.class.getPackage().getName();
		}

		private String className(final String className) {
			return className == null ? "Proxy" : className.replace('.', '_');
		}
	}
}
