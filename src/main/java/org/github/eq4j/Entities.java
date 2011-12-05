package org.github.eq4j;

import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.core.Predicate;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;

public final class Entities {

	private static NamingPolicy namingPolicy = new NamePolicy();
	private static final Class<?>[] INTERFACES = { Entity.class };
	private static final Class<?>[] CALLBACK_TYPES = { EntityProxy.class };
	private static Map<Class<?>, Factory> factoryCache = new HashMap<Class<?>, Factory>();

	public static <E> E entityOf(Class<E> entityType) {
		Factory factory = getOrCreateFactory(entityType);
		return entityType.cast(factory.newInstance(new EntityProxy()));
	}

	static <E> E entityOf(Class<E> entityType, Entity parent, String path) {
		Factory factory = getOrCreateFactory(entityType);
		return entityType.cast(factory.newInstance(new EntityProxy(parent, path)));
	}

	private static <E> Factory getOrCreateFactory(Class<E> entityType) {
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

	private static <E> Factory createFactory(Class<E> entityType) {
		Enhancer enhancer = new Enhancer();
		enhancer.setUseCache(false);
		enhancer.setUseFactory(true);
		enhancer.setSuperclass(entityType);
		enhancer.setInterfaces(INTERFACES);
		enhancer.setCallbackTypes(CALLBACK_TYPES);
		enhancer.setInterceptDuringConstruction(false);
		enhancer.setNamingPolicy(namingPolicy);
		return Factory.class.cast(enhancer.create());
	}

	private static final class NamePolicy implements NamingPolicy {
		@Override
		public String getClassName(String prefix, String source, Object key, Predicate names) {
			return prefix + "$$EnhancedByCglibForEQ4J" + key.hashCode();
		}
	}
}
