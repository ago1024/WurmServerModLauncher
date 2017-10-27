package org.gotti.wurmunlimited.mods.creatureagemod;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.gotti.wurmunlimited.modloader.callbacks.CallbackApi;
import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modsupport.creatures.CreatureTemplateParser;

import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.creatures.CreatureStatus;
import com.wurmonline.server.creatures.CreatureTemplateIds;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

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
		
		increaseGrowthUntilAge = Integer.valueOf(properties.getProperty("increaseGrowthUntilAge", Integer.toString(increaseGrowthUntilAge)));
		increaseGrowthTimer = Math.min(ORIG_CREATURE_POLL_TIMER, Long.valueOf(properties.getProperty("increaseGrowthTimer", Long.toString(increaseGrowthTimer))));
		
		final CreatureTemplateParser parser = new CreatureTemplateParser() {
			protected int unparsable(String name) {
				logger.warning("Invalid template " + name);
				return -1;
			};
		};
		
		String excl = properties.getProperty("excludedTemplates");
		if (excl != null) {
			excludedTemplates = Arrays.stream(parser.parseList(excl)).filter(i -> i != -1).boxed().collect(Collectors.toSet());
		}

		logger.log(Level.INFO, "increaseGrowthUntilAge: " + increaseGrowthUntilAge);
		logger.log(Level.INFO, "increaseGrowthTimer: " + increaseGrowthTimer);
		
		logger.log(Level.INFO, "excludedTemplates: " + parser.toString(excludedTemplates.stream().mapToInt(Integer::intValue).toArray()));
	}
	
	@CallbackApi
	public long getAdjustedLastPolledAge(CreatureStatus creatureStatus, boolean reborn) {

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
			
			final CtClass ctCreatureStatus = HookManager.getInstance().getClassPool().get("com.wurmonline.server.creatures.CreatureStatus");
			HookManager.getInstance().addCallback(ctCreatureStatus, "creatureagemod", this);
			
			CtMethod method = ctCreatureStatus.getMethod("pollAge", "(I)Z");
			method.instrument(new ExprEditor() {
				
				@Override
				public void edit(FieldAccess f) throws CannotCompileException {
					if ("lastPolledAge".equals(f.getFieldName())) {
						StringBuilder replacement = new StringBuilder();
						
						replacement.append("$_ = creatureagemod.getAdjustedLastPolledAge(this, reborn);");
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
