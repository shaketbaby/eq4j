package org.github.eq4j;


public final class Select<E> extends Statement<E> {
	
	public E from(Class<E> entityType) {
		return entity(entityType);
	}

	public void groupBy(Object field, Object... fields) {
		
	}

}
