package org.github.eq4j;

import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isPrivate;
import static org.github.eq4j.Entities.entityOf;
import static org.github.eq4j.Registry.registerPath;
import static org.github.eq4j.ValueFactory.uniqueValueFor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.InvocationHandler;

class EntityProxy implements InvocationHandler, Entity
{
	private final Path path;

	EntityProxy()
	{
		this(new Path("", null));
	}

	public EntityProxy(final Path path)
	{
		this.path = path;
	}

	@Override
	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
	{
		if (Entity.class.equals(method.getDeclaringClass())) {
			return method.invoke(this, args);
		}

		Class<?> returnType = method.getReturnType();
		if (isProxyable(returnType)) {
			return entityOf(returnType, new EntityProxy(newPath(proxy, method)));
		}
		return registerPath(uniqueValueFor(returnType), newPath(proxy, method));
	}

	private Path newPath(final Object proxy, final Method method)
	{
		final String name = method.getName();
		final String localPath = name.startsWith("get") ? name.charAt(3) + name.substring(4) : name;
		return new Path(localPath, Entity.class.cast(proxy).getPath_EQ4J());
	}

	private boolean isProxyable(final Class<?> type)
	{
		return isNotPrivateOrFinal(type.getModifiers()) && hasNoArgContructor(type);
	}

	private boolean isNotPrivateOrFinal(final int modifiers)
	{
		return !(isFinal(modifiers) || isPrivate(modifiers));
	}

	private boolean hasNoArgContructor(final Class<?> type) {
		for (Constructor<?> c : type.getDeclaredConstructors()) {
			if (isNotPrivateOrFinal(c.getModifiers()) &&
				c.getParameterTypes().length == 0) {
				return true;
			}
		}
		return false;
	}

	// --- implementation of the Entity interface

	@Override
	public void setAlias_EQ4J(final String alias)
	{
		path.setPath(alias);
	}

	@Override
	public Path getPath_EQ4J()
	{
		return path;
	}
}
