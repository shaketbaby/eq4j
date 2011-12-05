package org.github.eq4j;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.github.eq4j.test.TestEntityClass;
import org.junit.Test;

public class EntitiesTest {

	@Test
	public void entityProxyShouldBeTypeOfEntityInterface() {
		TestEntityClass testEntity = Entities.entityOf(TestEntityClass.class);
		assertThat(testEntity, instanceOf(Entity.class));
	}
	
	@Test
	public void shouldOnlyCreateOneProxyClassForEachEntityClass() {
		Object class1 = Entities.entityOf(TestEntityClass.class).getClass();
		Object class2 = Entities.entityOf(TestEntityClass.class).getClass();
		assertThat(class1, sameInstance(class2));
	}

}
