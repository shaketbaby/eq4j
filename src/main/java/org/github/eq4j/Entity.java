package org.github.eq4j;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

class Entity<E> implements MethodInterceptor {

	private final String alias;
	private final E proxy;
	private final Class<E> entityType;
	
	public Entity(Class<E> entityType, String alias) {
		this.entityType = entityType;
		this.alias = alias;
		this.proxy = createProxy(entityType);
	}
	
	public Class<E> getType() {
		return entityType;
	}
	
	public E getProxy() {
		return proxy;
	}

	public String getAlias() {
		return alias;
	}

	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		Field.pushField(this, method);

		if (isSimpleType(method.getReturnType())) {
			return proxy.invokeSuper(obj, args);
		}

		// return type is also an entity or a component
		return createProxy(method.getReturnType());
	}

	private <T> T createProxy(Class<T> entityType) {
		return entityType.cast(Enhancer.create(entityType, this));
	}

	private boolean isSimpleType(Class<?> returnType) {
		return false;
	}
}