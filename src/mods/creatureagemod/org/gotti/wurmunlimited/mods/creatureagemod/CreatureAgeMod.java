package org.gotti.wurmunlimited.mods.creatureagemod;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
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
import com.wurmonline.server.creatures.CreatureTemplateIds;

public class CreatureAgeMod implements WurmMod, Configurable {

	private static final long ORIG_CREATURE_POLL_TIMER = 2419200L;

	private int increaseGrowthUntilAge = 8;
	private long increaseGrowthTimer = 259200L;
	private Set<Integer> excludedTemplates = new HashSet<>(Arrays.asList(
			CreatureTemplateIds.BOAR_FO_CID,
			CreatureTemplateIds.HYENA_LIBILA_CID,
			CreatureTemplateIds.WORG_CID,
			CreatureTemplateIds.GORILLA_MAGRANON_CID,
			CreatureTemplateIds.ZOMBIE_CID,
			CreatureTemplateIds.SKELETON_CID
			));

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	public void configure(Properties properties) {
		Map<String, Integer> nameToId = new HashMap<>();
		Map<Integer, String> idToName = new HashMap<>();
		for (Field field : CreatureTemplateIds.class.getFields()) {
			String name = field.getName().toLowerCase();
			if (name.endsWith("_cid")) {
				name = name.replaceAll("_cid$", "");
				try {
					int id = field.getInt(CreatureTemplateIds.class);
					nameToId.put(name, id);
					idToName.put(id, name);
				} catch (IllegalAccessException e) {
					logger.log(Level.WARNING, null, e);
				}
			}
		}
		
		
		this.increaseGrowthUntilAge = Integer.valueOf(properties.getProperty("increaseGrowthUntilAge", Integer.toString(increaseGrowthUntilAge)));
		this.increaseGrowthTimer = Math.min(ORIG_CREATURE_POLL_TIMER, Long.valueOf(properties.getProperty("increaseGrowthTimer", Long.toString(increaseGrowthTimer))));
		
		String excl = properties.getProperty("excludedTemplates");
		if (excl != null) {
			excludedTemplates = new HashSet<>();
			for (String name : excl.split(",")) {
				Integer id = nameToId.get(name);
				if (id == null) {
					try {
						id = Integer.parseInt(name);
					} catch (NumberFormatException e) {
						id = null;
					}
				}
				if (id == null) {
					logger.warning("Invalid template " + id);
					continue;
				}
				excludedTemplates.add(id);
			}
		}

		logger.log(Level.INFO, "increaseGrowthUntilAge: " + increaseGrowthUntilAge);
		logger.log(Level.INFO, "increaseGrowthTimer: " + increaseGrowthTimer);
		
		Iterable<String> iterable = () -> excludedTemplates.stream().map((id) -> idToName.get(id)).iterator();
		logger.log(Level.INFO, "excludedTemplates: " + String.join(",", iterable));
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
							int templateId = creatureStatus.getTemplate().getTemplateId();

							// Check if the shorter timer applies and has elapsed
							if (!reborn && age < increaseGrowthUntilAge && WurmCalendar.currentTime - creatureStatus.lastPolledAge > increaseGrowthTimer && !excludedTemplates.contains(templateId)) {
								long origLastPolled = creatureStatus.lastPolledAge;

								// Set a fake last polled time earlier in time
								long newLastPolled = WurmCalendar.currentTime - ORIG_CREATURE_POLL_TIMER - 1;
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
