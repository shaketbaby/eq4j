package org.github.eq4j;

interface Entity {
	
	String getPath();
	
	Entity getParent_EQ4J();

	String getAlias_EQ4J();
	void setAlias_EQ4J(String alias);
}
