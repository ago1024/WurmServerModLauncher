package org.gotti.wurmunlimited.modloader;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.classhooks.InvocationHandlerFactory;

/**
 * Hook into com.wurmonline.server.Server.startRunning()
 * 
 * The InvocationHandler calls startRunning() first, then fires onServerStarted event
 */
public class ProxyServerHook extends ServerHook {

	public ProxyServerHook() {

		InvocationHandlerFactory invocationHandlerFactory = new InvocationHandlerFactory() {

			@Override
			public InvocationHandler createInvocationHandler() {
				return new InvocationHandler() {

					@Override
					public Object invoke(Object wrapped, Method method, Object[] args) throws Throwable {
						Object result = method.invoke(wrapped, args);
						fireOnServerStarted();
						return result;
					}
				};
			}
		};

		HookManager.getInstance().registerHook("com.wurmonline.server.Server", "startRunning", "()V", invocationHandlerFactory);
	}
}
