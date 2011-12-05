package org.github.eq4j;

import static org.github.eq4j.Entities.entityOf;

import java.lang.reflect.Method;

import javax.persistence.Embeddable;

import net.sf.cglib.proxy.InvocationHandler;

class EntityProxy implements Entity, InvocationHandler {

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (Entity.class.equals(method.getDeclaringClass())) {
			return method.invoke(this, args);
		}
		String path = getFieldPath(method);
		
		Class<?> returnType = method.getReturnType();
		if (isComplexType(returnType)) {
			return entityOf(returnType, Entity.class.cast(proxy), path);
		}
		if (Void.TYPE == returnType) {
			return null;
		}
		// TODO generate unique value and store it into cache for later retrieval.
		return null;
	}
	
	private String getFieldPath(Method method) {
		final String name = method.getName();
		return name.startsWith("get") ? name.charAt(3) + name.substring(4) : name;
	}

	private boolean isComplexType(Class<?> type) {
		return type.isAnnotationPresent(javax.persistence.Entity.class) ||
			   type.isAnnotationPresent(Embeddable.class);
	}

	// --- implementation of the Entity interface

	private String alias;
	private Entity parent;

	EntityProxy() {
		this.parent = null;
	}
	
	EntityProxy(Entity parent, String alias) {
		this.parent = parent;
		this.alias = alias;
	}
	
	public String getPath() {
		return parent == null ? alias : parent.getPath() + "." + alias;
	}

	@Override
	public String getAlias_EQ4J() {
		return alias;
	}

	@Override
	public void setAlias_EQ4J(String alias) {
		this.alias = alias;
		this.parent = null;
	}

	@Override
	public Entity getParent_EQ4J() {
		return parent;
	}
}