package org.gotti.wurmunlimited.mods.creatureagemod;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;

import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.creatures.CreatureStatus;
import com.wurmonline.server.creatures.CreatureTemplateIds;

public class CreatureAgeMod implements WurmServerMod, Configurable, Initable, PreInitable {

	private static final long ORIG_CREATURE_POLL_TIMER = 2419200L;

	private static int increaseGrowthUntilAge = 8;
	private static long increaseGrowthTimer = 259200L;
	private static Set<Integer> excludedTemplates = new HashSet<>(Arrays.asList(
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
		
		
		increaseGrowthUntilAge = Integer.valueOf(properties.getProperty("increaseGrowthUntilAge", Integer.toString(increaseGrowthUntilAge)));
		increaseGrowthTimer = Math.min(ORIG_CREATURE_POLL_TIMER, Long.valueOf(properties.getProperty("increaseGrowthTimer", Long.toString(increaseGrowthTimer))));
		
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
	
	public static long getAdjustedLastPolledAge(CreatureStatus creatureStatus, boolean reborn) {

		int age = creatureStatus.age;
		int templateId = creatureStatus.getTemplate().getTemplateId();

		if (!reborn && age < increaseGrowthUntilAge && WurmCalendar.currentTime - creatureStatus.lastPolledAge > increaseGrowthTimer && !excludedTemplates.contains(templateId)) {
			return WurmCalendar.currentTime - ORIG_CREATURE_POLL_TIMER - 1;
		}
		
		return creatureStatus.lastPolledAge;
	}
	
	@Override
	public void preInit() {
		try {
		
			CtClass ctCreatureStatus = HookManager.getInstance().getClassPool().get("com.wurmonline.server.creatures.CreatureStatus");
			CtMethod method = ctCreatureStatus.getMethod("pollAge", "(I)Z");
			method.instrument(new ExprEditor() {
				
				@Override
				public void edit(FieldAccess f) throws CannotCompileException {
					if ("lastPolledAge".equals(f.getFieldName())) {
						StringBuilder replacement = new StringBuilder();
						
						replacement.append(String.format("$_ = %s#getAdjustedLastPolledAge(this, reborn);", CreatureAgeMod.class.getName()));
						f.replace(replacement.toString());
					}
				}
			});
		
		} catch (NotFoundException | CannotCompileException e ) {
			throw new HookException(e);
		}
	}

	@Override
	public void init() {
	}

}
