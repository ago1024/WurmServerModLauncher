package org.gotti.wurmunlimited.mods.spellmod;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.HookBuilder;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;

import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.deities.Deities;
import com.wurmonline.server.deities.Deity;
import com.wurmonline.server.players.DbPlayerInfo;
import com.wurmonline.server.spells.Spell;

public class SpellMod implements WurmMod, Configurable, ServerStartedListener {

	private Integer favorLimit = Integer.MAX_VALUE;
	private boolean removePriestRestrictions = true;
	private boolean allowAllSpells = true;
	private boolean allowLightSpells = true;
	private boolean unlimitedPrayers = false;
	private boolean noPrayerDelay = false;
	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	public void onServerStarted() {
		logger.log(Level.INFO, "Initializing Spell modifications");

		Set<Spell> allGodSpells = new TreeSet<>();
		Set<Spell> whiteLightSpells = new TreeSet<>();
		Set<Spell> blackLightSpells = new TreeSet<>();

		for (Deity deity : Deities.getDeities()) {
			allGodSpells.addAll(deity.getSpells());
			if (deity.isHateGod()) {
				blackLightSpells.addAll(deity.getSpells());
			} else {
				whiteLightSpells.addAll(deity.getSpells());
			}
		}

		try {
			Field buildWallBonus = ReflectionUtil.getField(Deity.class, "buildWallBonus");
			Field roadProtector = ReflectionUtil.getField(Deity.class, "roadProtector");
			Field cost = ReflectionUtil.getField(Spell.class, "cost");

			Field isAllowVynora = ReflectionUtil.getField(ActionEntry.class, "isAllowVynora");
			Field isAllowFo = ReflectionUtil.getField(ActionEntry.class, "isAllowFo");
			Field isAllowMagranon = ReflectionUtil.getField(ActionEntry.class, "isAllowMagranon");
			Field isAllowLibila = ReflectionUtil.getField(ActionEntry.class, "isAllowLibila");

			// Make all spells available to all gods
			for (Deity deity : Deities.getDeities()) {
				if (allowAllSpells || allowLightSpells) {
					final Set<Spell> spells;
					if (allowAllSpells) {
						spells = allGodSpells;
					} else if (deity.isHateGod()) {
						spells = blackLightSpells;
					} else {
						spells = whiteLightSpells;
					}

					for (Spell spell : spells) {
						if (!deity.getSpells().contains(spell)) {
							deity.addSpell(spell);
						}
					}
				}

				if (removePriestRestrictions) {
					try {
						ReflectionUtil.setPrivateField(deity, buildWallBonus, Float.valueOf(0.0f));
						ReflectionUtil.setPrivateField(deity, roadProtector, Boolean.TRUE);
					} catch (IllegalAccessException | IllegalArgumentException | ClassCastException e) {
						logger.log(Level.WARNING, e.getMessage(), e);
					}
				}
			}

			if (favorLimit < Integer.MAX_VALUE) {
				for (Spell spell : allGodSpells) {
					if (spell.getCost(false) > favorLimit) {
						try {
							ReflectionUtil.setPrivateField(spell, cost, Integer.valueOf(favorLimit));
						} catch (IllegalAccessException | IllegalArgumentException | ClassCastException e) {
							logger.log(Level.WARNING, e.getMessage(), e);
						}

					}
				}
			}

			if (removePriestRestrictions) {
				for (ActionEntry action : Actions.actionEntrys) {
					try {
						ReflectionUtil.setPrivateField(action, isAllowVynora, Boolean.TRUE);
						ReflectionUtil.setPrivateField(action, isAllowFo, Boolean.TRUE);
						ReflectionUtil.setPrivateField(action, isAllowMagranon, Boolean.TRUE);
						ReflectionUtil.setPrivateField(action, isAllowLibila, Boolean.TRUE);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						logger.log(Level.WARNING, e.getMessage(), e);
					}
				}
			}

		} catch (NoSuchFieldException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
	}

	@Override
	public void configure(Properties properties) {
		removePriestRestrictions = Boolean.parseBoolean(properties.getProperty("removePriestRestrictions", Boolean.toString(removePriestRestrictions)));
		favorLimit = Integer.parseInt(properties.getProperty("favorLimit", Integer.toString(favorLimit)));
		allowAllSpells = Boolean.parseBoolean(properties.getProperty("allowAllSpells", Boolean.toString(allowAllSpells)));
		allowLightSpells = Boolean.parseBoolean(properties.getProperty("allowLightSpells", Boolean.toString(allowLightSpells)));
		unlimitedPrayers = Boolean.parseBoolean(properties.getProperty("unlimitedPrayers", Boolean.toString(unlimitedPrayers)));
		noPrayerDelay = Boolean.parseBoolean(properties.getProperty("noPrayerDelay", Boolean.toString(noPrayerDelay)));

		logger.log(Level.INFO, "removePriestRestrictions: " + removePriestRestrictions);
		logger.log(Level.INFO, "favorLimit: " + favorLimit);
		logger.log(Level.INFO, "allowAllSpells: " + allowAllSpells);
		logger.log(Level.INFO, "allowLightSpells: " + allowLightSpells);
		logger.log(Level.INFO, "unlimitedPrayers: " + unlimitedPrayers);
		logger.log(Level.INFO, "noPrayerDelay: " + noPrayerDelay);

		if (unlimitedPrayers || noPrayerDelay) {
			HookBuilder.getInstance().registerHook("com.wurmonline.server.players.DbPlayerInfo", "setNumFaith", "(BJ)V", new InvocationHandler() {

				@Override
				public Object invoke(Object object, Method method, Object[] args) throws Throwable {
					DbPlayerInfo dbPlayerInfo = (DbPlayerInfo) object;
					if (unlimitedPrayers) {
						args[0] = dbPlayerInfo.numFaith = 0;
					}
					if (noPrayerDelay) {
						args[1] = dbPlayerInfo.lastFaith = 0;
					}

					return method.invoke(object, args);
				}
			});
		}
	}
}
