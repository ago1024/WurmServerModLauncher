package org.gotti.wurmunlimited.mods.creatureagemod;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.classhooks.InvocationHandlerFactory;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;

import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.creatures.CreatureStatus;

public class CreatureAgeMod implements WurmMod, Configurable {

	private static final long ORIG_CREATURE_POLL_TIMER = 2419200L;

	private int increaseGrowthUntilAge = 8;
	private long increaseGrowthTimer = 259200L;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	public void configure(Properties properties) {
		this.increaseGrowthUntilAge = Integer.valueOf(properties.getProperty("increaseGrowthUntilAge", Integer.toString(increaseGrowthUntilAge)));
		this.increaseGrowthTimer = Math.min(ORIG_CREATURE_POLL_TIMER, Long.valueOf(properties.getProperty("increaseGrowthTimer", Long.toString(increaseGrowthTimer))));

		logger.log(Level.INFO, "increaseGrowthUntilAge: " + increaseGrowthUntilAge);
		logger.log(Level.INFO, "increaseGrowthTimer: " + increaseGrowthTimer);

	}

	@Override
	public void init() {

		HookManager.getInstance().registerHook("com.wurmonline.server.creatures.CreatureStatus", "pollAge", "(I)Z", new InvocationHandlerFactory() {

			@Override
			public InvocationHandler createInvocationHandler() {
				try {

					return new InvocationHandler() {

						Field field = ReflectionUtil.getField(CreatureStatus.class, "reborn");

						@Override
						public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
							CreatureStatus creatureStatus = (CreatureStatus) proxy;

							Boolean value = ReflectionUtil.getPrivateField(creatureStatus, field);
							boolean reborn = value != null && value.booleanValue();
							int age = creatureStatus.age;

							// Check if the shorter timer applies and has elapsed
							if (!reborn && age < increaseGrowthUntilAge && WurmCalendar.currentTime - creatureStatus.lastPolledAge > increaseGrowthTimer) {
								long origLastPolled = creatureStatus.lastPolledAge;

								// Set a fake last polled time earlier in time
								long newLastPolled = WurmCalendar.currentTime - ORIG_CREATURE_POLL_TIMER;
								creatureStatus.lastPolledAge = newLastPolled;

								// run pollAge()
								Object result = method.invoke(proxy, args);

								// Check if pollAge did not set a new last polled time (i.e. it did not increase the age for some reason). Revert to the original value
								if (creatureStatus.lastPolledAge == newLastPolled) {
									creatureStatus.lastPolledAge = origLastPolled;
								}

								return result;
							} else {
								return method.invoke(proxy, args);
							}
						}
					};
				} catch (NoSuchFieldException e) {
					throw new HookException(e);
				}
			}
		});
	}

}
