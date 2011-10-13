package org.github.eq4j;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public final class Select<E> {
	
	private Entity<E> entity;

	public E from(Class<E> entityType) {
		this.entity = new Entity<E>(entityType);
		return entity.getProxy();
	}

	public <F> Condition where(F field, Operation<F> operation) {
		return null;
	}

	public void groupBy(Object field, Object... fields) {
		
	}

	public TypedQuery<E> toJPAQuery(EntityManager em) {
		return em.createQuery("", entity.getType());
	}

}
