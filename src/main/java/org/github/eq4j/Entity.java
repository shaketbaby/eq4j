package org.github.eq4j;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

class Entity<E> implements MethodInterceptor {

	private E proxy;
	private final Class<E> entityType;

	public Entity(Class<E> entityType) {
		this.entityType = entityType;
		this.proxy = entityType.cast(Enhancer.create(entityType, this));
	}
	
	public Class<E> getType() {
		return entityType;
	}
	
	public E getProxy() {
		return proxy;
	}

	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		return null;
	}

}
