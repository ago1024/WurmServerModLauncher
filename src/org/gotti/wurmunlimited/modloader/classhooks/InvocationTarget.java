package org.gotti.wurmunlimited.modloader.classhooks;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class InvocationTarget {
	
	private boolean staticMethod;
	
	private String methodName;
	
	private String identifier;
	
	private Method method;
	
	private InvocationHandler invocationHandler;

	private Class<?>[] exceptionTypes;
	
	public InvocationTarget(InvocationHandler invocationHandler, boolean staticMethod, String methodName, String identifier, Class<?>[] exceptionTypes) {
		this.setMethod(null);
		this.setMethodName(methodName);
		this.setStaticMethod(staticMethod);
		this.setIdentifier(identifier);
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
	
	public String getIdentifier() {
		return identifier;
	}
	
	protected void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public boolean isStaticMethod() {
		return staticMethod;
	}
	
	protected void setStaticMethod(boolean staticMethod) {
		this.staticMethod = staticMethod;
	}
}
