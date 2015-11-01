package org.gotti.wurmunlimited.mods.disciomod;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.classhooks.InvocationHandlerFactory;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;

import com.wurmonline.server.Server;

public class DiscIOMod implements WurmMod, Configurable {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	public void configure(Properties properties) {
		logger.log(Level.INFO, "fix disc io problem");
	}

	@Override
	public void init() {
		HookManager.getInstance().registerHook("com.wurmonline.server.zones.Zones", "saveProtectedTiles", "()V", new InvocationHandlerFactory() {
			
			@Override
			public InvocationHandler createInvocationHandler() {
				return new InvocationHandler() {
					@Override
					public Object invoke(Object object, Method method, Object[] args) throws Throwable {
						Field lastResetTiles = ReflectionUtil.getField(Server.class, "lastResetTiles");
						ReflectionUtil.setPrivateField(Server.class, lastResetTiles, System.currentTimeMillis());
						return method.invoke(object, args);
					}
				};
			}
		});
	}
}
