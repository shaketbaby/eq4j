package org.github.eq4j;

import static java.lang.Character.toLowerCase;

import java.lang.reflect.Method;

public final class Field {
	
	private static ThreadLocal<Field> current = new ThreadLocal<Field>();
	
	static Field popField() {
		Field result = current.get();
		current.set(null);
		return result;
	}
	
	static void pushField(Entity<?> entity, Method method) {
		String fieldName = determineFieldName(method.getName());
		Field currentField = current.get();
		if (currentField == null) {
			current.set(new Field(entity.getAlias() + "." + fieldName));
		} else {
			currentField.fieldName = currentField.fieldName + "." + fieldName;
		}
	}
	
	private static String determineFieldName(String methodName) {
		return methodName.startsWith("get") ? decapitalize(methodName.substring(3)) : methodName;
	}
	
	private static String decapitalize(String s) {
		return toLowerCase(s.charAt(0)) + s.substring(1);
	}

	private String fieldName;
	
	private Field(String fieldName) {
		this.fieldName = fieldName;
	}

	@Override
	public String toString() {
		return fieldName;
	}
}
