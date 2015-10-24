package org.gotti.wurmunlimited.mods.spellmod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.mods.WurmMod;

import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.deities.Deities;
import com.wurmonline.server.deities.Deity;
import com.wurmonline.server.spells.Spell;

public class SpellMod implements WurmMod {
	
	private List<Field> getAllFields(Class<?> clazz) {
		List<Field> currentClassFields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
		Class<?> parentClass = clazz.getSuperclass();
		
		if (parentClass != null && !parentClass.equals(Object.class)) {
			List<Field> parentClassFields = getAllFields(parentClass);
			currentClassFields.addAll(parentClassFields);
		}
		
		return currentClassFields;
	}
	
	private Field getField(Object object, String fieldName) throws NoSuchFieldException {
		for (Field field : getAllFields(object.getClass())) {
			if (field.getName().equals(fieldName)) {
				return field;
			}
		}
		throw new NoSuchFieldException(fieldName);
	}
	
	
	private <T> void setPrivateField(Object object, String fieldName, T value) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, ClassCastException {
		Field field = getField(object, fieldName);
		boolean isAccesible = field.isAccessible();
		field.setAccessible(true);
		try {
			field.set(object, value);
		} finally {
			field.setAccessible(isAccesible);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T getPrivateField(Object object, String fieldName) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, ClassCastException {
		Field field = getField(object, fieldName);
		boolean isAccesible = field.isAccessible();
		field.setAccessible(true);
		try {
			return (T) field.get(object);
		} finally {
			field.setAccessible(isAccesible);
		}
	}
	
	@Override
	public void onServerStarted() {
		Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Initializing Spell modifications");
		
		Set<Spell> allGodSpells = new TreeSet<>();
		
		// Make all spells available to all gods
		for (Deity deity : Deities.getDeities()) {
			allGodSpells.addAll(deity.getSpells());
		}
		
		for (Deity deity : Deities.getDeities()) {
			for (Spell spell : allGodSpells) {
				if (!deity.getSpells().contains(spell)) {
					deity.addSpell(spell);
				}
			}
			
			try {
				setPrivateField(deity, "buildWallBonus", Float.valueOf(0.0f));
				setPrivateField(deity, "roadProtector", Boolean.TRUE);
			} catch (IllegalAccessException | NoSuchFieldException | IllegalArgumentException | ClassCastException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getMessage(), e);
			}
		}
		
		for (Spell spell : allGodSpells) {
			if (spell.getCost(false) > 90) {
				try {
					setPrivateField(spell, "cost", Integer.valueOf(90));
				} catch (IllegalAccessException | NoSuchFieldException | IllegalArgumentException | ClassCastException e) {
					Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getMessage(), e);
				}
				
			}
		}
		
		for (ActionEntry action : Actions.actionEntrys) {
			try {
				setPrivateField(action, "isAllowVynora", Boolean.TRUE);
				setPrivateField(action, "isAllowFo", Boolean.TRUE);
				setPrivateField(action, "isAllowMagranon", Boolean.TRUE);
				setPrivateField(action, "isAllowLibila", Boolean.TRUE);
			} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getMessage(), e);
			}
		}
	}
}
