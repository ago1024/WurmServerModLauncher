package org.gotti.wurmunlimited.mods;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ReflectionUtil {

	private static List<Field> getAllFields(Class<?> clazz) {
		List<Field> currentClassFields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
		Class<?> parentClass = clazz.getSuperclass();

		if (parentClass != null && !parentClass.equals(Object.class)) {
			List<Field> parentClassFields = getAllFields(parentClass);
			currentClassFields.addAll(parentClassFields);
		}

		return currentClassFields;
	}

	public static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
		for (Field field : getAllFields(clazz)) {
			if (field.getName().equals(fieldName)) {
				return field;
			}
		}
		throw new NoSuchFieldException(fieldName);
	}

	public static <T> void setPrivateField(Object object, Field field, T value) throws IllegalArgumentException, IllegalAccessException, ClassCastException {
		boolean isAccesible = field.isAccessible();
		field.setAccessible(true);
		try {
			field.set(object, value);
		} finally {
			field.setAccessible(isAccesible);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getPrivateField(Object object, Field field) throws IllegalArgumentException, IllegalAccessException, ClassCastException {
		boolean isAccesible = field.isAccessible();
		field.setAccessible(true);
		try {
			return (T) field.get(object);
		} finally {
			field.setAccessible(isAccesible);
		}
	}

	
}
