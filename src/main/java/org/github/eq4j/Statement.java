package org.github.eq4j;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public abstract class Statement<E> {

	private int aliasSeq = 1;
	private Entity<E> entity;
	
	public E entity(Class<E> entityType) {
		this.entity = new Entity<E>(entityType, this.newAlias());
		return entity.getProxy();
	}

	public <F> Condition where(F someField, Operation<F> operation) {
		Field field = Field.popField();
		
		return null;
	}

	public TypedQuery<E> toJPAQuery(EntityManager em) {
		return em.createQuery(generateQueryString(), entity.getType());
	}
	
	protected String generateQueryString() {
		// TODO
		return "";
	}

	String newAlias() {
		return "e" + (aliasSeq++);
	}
}
