package org.gotti.wurmunlimited.modloader.classhooks;

import java.lang.reflect.InvocationHandler;

public interface InvocationHandlerFactory {
	
	InvocationHandler createInvocationHandler();

}
