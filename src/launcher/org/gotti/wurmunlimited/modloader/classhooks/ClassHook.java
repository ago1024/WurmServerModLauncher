package org.gotti.wurmunlimited.modloader.classhooks;


public class ClassHook {
	private String methodName;
	private String methodType;
	private InvocationHandlerFactory invocationHandlerFactory;

	public ClassHook(String methodName, String methodType, InvocationHandlerFactory invocationHandlerFactory) {
		this.setMethodName(methodName);
		this.setMethodType(methodType);
		this.setInvocationHandlerFactory(invocationHandlerFactory);
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

	public InvocationHandlerFactory getInvocationHandlerFactory() {
		return invocationHandlerFactory;
	}

	protected void setInvocationHandlerFactory(InvocationHandlerFactory invocationHandlerFactory) {
		this.invocationHandlerFactory = invocationHandlerFactory;
	}
}
