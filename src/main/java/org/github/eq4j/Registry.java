package org.github.eq4j;

import java.util.Map;
import java.util.WeakHashMap;

class Registry
{
	private static final ThreadLocal<Map<Object, Path>> uniqueValues = threadLocal();

	public static Object registerPath(final Object value, final Path path) {
		uniqueValues.get().put(value, path);
		return value;
	}

	private static <K, V> ThreadLocal<Map<K, V>> threadLocal() {
		return new ThreadLocal<Map<K, V>>() {
			@Override
			protected Map<K, V> initialValue()
			{
				return new WeakHashMap<K, V>();
			}
		};
	}
}
