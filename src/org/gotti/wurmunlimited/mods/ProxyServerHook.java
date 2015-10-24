package org.gotti.wurmunlimited.mods;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.wurmonline.server.Server;

public class ProxyServerHook extends ServerHook {

	public ProxyServerHook() {

		Server instance = Server.getInstance();
		Server serverProxy = (Server) (Proxy.newProxyInstance(Server.class.getClassLoader(), new Class[] { Server.class }, new ServerInvocationHandler(instance)));
		
		try {
			Field field = ReflectionUtil.getField(Server.class, "instance");
			ReflectionUtil.setPrivateField(Server.class, field, serverProxy);
		} catch (IllegalAccessException | ClassCastException | NoSuchFieldException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getMessage(), e);
		}
	}

	public class ServerInvocationHandler implements InvocationHandler {
		private Server wrapped;

		public ServerInvocationHandler(Server server) {
			wrapped = server;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Object result = method.invoke(wrapped, args);
			if ("startRunning".equals(method.getName())) {
				fireOnServerStarted();
			}
			return result;
		}
	}

}
