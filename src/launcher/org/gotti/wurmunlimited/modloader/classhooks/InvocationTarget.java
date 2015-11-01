package org.gotti.wurmunlimited.modloader.classhooks;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.gotti.wurmunlimited.modloader.ReflectionUtil;

public class InvocationTarget {
	
	private boolean staticMethod;
	
	private String methodName;
	
	private String identifier;
	
	private Method method;
	
	private InvocationHandlerFactory invocationHandlerFactory;
	
	private InvocationHandler invocationHandler;

	private Class<?>[] exceptionTypes;
	
	public InvocationTarget(InvocationHandlerFactory invocationHandlerFactory, boolean staticMethod, String methodName, String identifier, Class<?>[] exceptionTypes) {
		this.setMethod(null);
		this.setMethodName(methodName);
		this.setStaticMethod(staticMethod);
		this.setIdentifier(identifier);
		this.setInvocationHandlerFactory(invocationHandlerFactory);
		this.setExceptionTypes(exceptionTypes);
	}

	public Method resolveMethod(Class<? extends Object> targetClass) throws NoSuchMethodException {
		if (getMethod() != null) {
			return getMethod();
		}
		method = ReflectionUtil.getMethod(targetClass, getMethodName());
		return method;
	}
	
	public InvocationHandler resolveInvocationHandler() {
		if (invocationHandler != null) {
			return invocationHandler;
		}
		invocationHandler = invocationHandlerFactory.createInvocationHandler();
		return invocationHandler;
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

	public InvocationHandlerFactory getInvocationHandlerFactory() {
		return invocationHandlerFactory;
	}

	protected void setInvocationHandlerFactory(InvocationHandlerFactory invocationHandlerFactory) {
		this.invocationHandlerFactory = invocationHandlerFactory;
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
