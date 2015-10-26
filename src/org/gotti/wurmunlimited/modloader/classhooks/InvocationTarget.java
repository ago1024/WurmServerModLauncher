package org.gotti.wurmunlimited.modloader.classhooks;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class InvocationTarget {
	
	private String methodName;
	
	private Method method;
	
	private InvocationHandler invocationHandler;

	private Class<?>[] exceptionTypes;
	
	public InvocationTarget(InvocationHandler invocationHandler, String methodName, Class<?>[] exceptionTypes) {
		this.setMethod(null);
		this.setMethodName(methodName);
		this.setInvocationHandler(invocationHandler);
		this.setExceptionTypes(exceptionTypes);
	}

	public Method resolveMethod(Class<? extends Object> targetClass) throws NoSuchMethodException {
		if (getMethod() != null) {
			return getMethod();
		}
		for (Method m : targetClass.getDeclaredMethods()) {
			if (m.getName().equals(getMethodName())) {
				setMethod(m);
				return getMethod();
			}
		}
		throw new NoSuchMethodException(getMethodName());
	}

	public String getMethodName() {
		return methodName;
	}

	protected void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Method getMethod() {
		return method;
	}

	protected void setMethod(Method method) {
		this.method = method;
	}

	public InvocationHandler getInvocationHandler() {
		return invocationHandler;
	}

	protected void setInvocationHandler(InvocationHandler invocationHandler) {
		this.invocationHandler = invocationHandler;
	}

	public Class<?>[] getExceptionTypes() {
		return exceptionTypes;
	}

	protected void setExceptionTypes(Class<?>[] exceptionTypes) {
		this.exceptionTypes = exceptionTypes;
	}
}
