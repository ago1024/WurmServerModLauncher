package org.gotti.wurmunlimited.modloader;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.gotti.wurmunlimited.modloader.classhooks.HookBuilder;

public class ProxyServerHook extends ServerHook {

	public ProxyServerHook() {

		HookBuilder.getInstance().registerHook("com.wurmonline.server.Server", "startRunning", "()V", new InvocationHandler() {
			
			@Override
			public Object invoke(Object wrapped, Method method, Object[] args) throws Throwable {
				Object result = method.invoke(wrapped, args);
				fireOnServerStarted();
				return result;
			}
		});
	}
}
