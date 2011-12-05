package org.github.eq4j;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;


public final class Select {
	
	public <A> Select column(A pathAttribute) {
		// TODO capture path attribute item
		return this;
	}
	
	public <E> From from(E entityProxy) {
		Entity.class.cast(entityProxy).setAlias_EQ4J("");
		return new From(this);
	}

	public <R> TypedQuery<R> asTypedQuery(EntityManager em, Class<R> resultClass) {
		// TODO query statement
		return em.createQuery("", resultClass);
	}
}
