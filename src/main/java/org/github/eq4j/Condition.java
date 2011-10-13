package org.github.eq4j;

interface Condition {

	<F> Condition and(F field, Operation<F> operation);

	<F> Condition or(F field, Operation<F> operation);

	
}
