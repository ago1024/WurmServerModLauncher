package org.gotti.wurmunlimited.modloader.classhooks;

import java.lang.reflect.InvocationHandler;

public class ClassHook {
	private String methodName;
	private String methodType;
	private InvocationHandler invocationHandler;

	public ClassHook(String methodName, String methodType, InvocationHandler invocationHandler) {
		this.setMethodName(methodName);
		this.setMethodType(methodType);
		this.setInvocationHandler(invocationHandler);
	}

	public String getMethodName() {
		return methodName;
	}

	protected void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getMethodType() {
		return methodType;
	}

	protected void setMethodType(String methodType) {
		this.methodType = methodType;
	}

	public InvocationHandler getInvocationHandler() {
		return invocationHandler;
	}

	protected void setInvocationHandler(InvocationHandler invocationHandler) {
		this.invocationHandler = invocationHandler;
	}
}
