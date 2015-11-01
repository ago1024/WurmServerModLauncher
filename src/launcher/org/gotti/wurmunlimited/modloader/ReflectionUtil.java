package org.gotti.wurmunlimited.modloader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

	private static List<Method> getAllMethods(Class<?> clazz) {
		List<Method> currentClassMethods = new ArrayList<>(Arrays.asList(clazz.getDeclaredMethods()));
		Class<?> parentClass = clazz.getSuperclass();

		if (parentClass != null && !parentClass.equals(Object.class)) {
			List<Method> parentClassFields = getAllMethods(parentClass);
			currentClassMethods.addAll(parentClassFields);
		}

		return currentClassMethods;
	}
	
	public static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
		for (Field field : getAllFields(clazz)) {
			if (field.getName().equals(fieldName)) {
				return field;
			}
		}
		throw new NoSuchFieldException(fieldName);
	}

	public static Method getMethod(Class<?> clazz, String methodName) throws NoSuchMethodException {
		for (Method method : getAllMethods(clazz)) {
			if (method.getName().equals(methodName)) {
				return method;
			}
		}
		throw new NoSuchMethodException(methodName);
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
	public static <T> T getPrivateField(Object object, Field field) throws IllegalArgumentException, IllegalAccessException, ClassCastException {
		boolean isAccesible = field.isAccessible();
		field.setAccessible(true);
		try {
			return (T) field.get(object);
		} finally {
			field.setAccessible(isAccesible);
		}
	}

	
}
