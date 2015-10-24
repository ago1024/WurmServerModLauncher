package org.gotti.wurmunlimited.mods.spellmod;

import java.lang.reflect.Field;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.mods.Configurable;
import org.gotti.wurmunlimited.mods.ReflectionUtil;
import org.gotti.wurmunlimited.mods.ServerStartedListener;
import org.gotti.wurmunlimited.mods.WurmMod;

import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.deities.Deities;
import com.wurmonline.server.deities.Deity;
import com.wurmonline.server.spells.Spell;

public class SpellMod implements WurmMod, Configurable, ServerStartedListener {

	private Integer favorLimit = Integer.MAX_VALUE;
	private boolean removePriestRestrictions = true;
	private boolean allowAllSpells = true;

	@Override
	public void onServerStarted() {
		Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Initializing Spell modifications");

		Set<Spell> allGodSpells = new TreeSet<>();

		// Make all spells available to all gods
		for (Deity deity : Deities.getDeities()) {
			allGodSpells.addAll(deity.getSpells());
		}

		try {
			Field buildWallBonus = ReflectionUtil.getField(Deity.class, "buildWallBonus");
			Field roadProtector = ReflectionUtil.getField(Deity.class, "roadProtector");
			Field cost = ReflectionUtil.getField(Spell.class, "cost");

			Field isAllowVynora = ReflectionUtil.getField(ActionEntry.class, "isAllowVynora");
			Field isAllowFo = ReflectionUtil.getField(ActionEntry.class, "isAllowFo");
			Field isAllowMagranon = ReflectionUtil.getField(ActionEntry.class, "isAllowMagranon");
			Field isAllowLibila = ReflectionUtil.getField(ActionEntry.class, "isAllowLibila");

			for (Deity deity : Deities.getDeities()) {
				if (allowAllSpells) {
					for (Spell spell : allGodSpells) {
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
						Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getMessage(), e);
					}
				}
			}

			if (favorLimit < Integer.MAX_VALUE) {
				for (Spell spell : allGodSpells) {
					if (spell.getCost(false) > favorLimit) {
						try {
							ReflectionUtil.setPrivateField(spell, cost, Integer.valueOf(favorLimit));
						} catch (IllegalAccessException | IllegalArgumentException | ClassCastException e) {
							Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getMessage(), e);
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
						Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getMessage(), e);
					}
				}
			}

		} catch (NoSuchFieldException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getMessage(), e);
		}
	}

	@Override
	public void configure(Properties properties) {
		removePriestRestrictions = Boolean.parseBoolean(properties.getProperty("removePriestRestrictions", Boolean.toString(removePriestRestrictions)));
		favorLimit = Integer.parseInt(properties.getProperty("favorLimit", Integer.toString(favorLimit)));
		allowAllSpells = Boolean.parseBoolean(properties.getProperty("allowAllSpells", Boolean.toString(allowAllSpells)));
	}
}
