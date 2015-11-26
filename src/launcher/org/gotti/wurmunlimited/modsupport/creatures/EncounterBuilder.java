package org.gotti.wurmunlimited.modsupport.creatures;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.wurmonline.server.zones.EncounterType;
import com.wurmonline.server.zones.SpawnTable;

public class EncounterBuilder {

	private byte tiletype;
	private byte elevation;
	private Object encounter;
	
	private static Class<?> encounterClass;
	private static Method encounterAddType;
	private static Method encounterTypeAddEncounter;
	
	static {
		try {
			encounterClass = Class.forName("com.wurmonline.server.zones.Encounter");
			encounterAddType = encounterClass.getDeclaredMethod("addType", new Class[] { int.class, int.class });
			encounterAddType.setAccessible(true);
			encounterTypeAddEncounter = EncounterType.class.getDeclaredMethod("addEncounter", new Class[] { encounterClass, int.class});
			encounterTypeAddEncounter.setAccessible(true);
		} catch (ClassNotFoundException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	public EncounterBuilder(byte tiletype) {
		this(tiletype, (byte) 0);
	}

	public EncounterBuilder(byte tiletype, byte elevation) {
		this.tiletype = tiletype;
		this.elevation = elevation;
		
		try {
			encounter = encounterClass.newInstance();
		} catch (IllegalAccessException | IllegalArgumentException | InstantiationException e) {
			throw new RuntimeException(e);
		}
	}
	
	public EncounterBuilder addCreatures(int templateId, int count) {
		
		try {
			encounterAddType.invoke(encounter, templateId, count);
		} catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	public void build(int chance) {
		if (encounter == null || chance == 0) {
			return;
		}

		try {
			EncounterType encounterType = SpawnTable.getType(tiletype, elevation);
			encounterTypeAddEncounter.invoke(encounterType, encounter, chance);
		} catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
			throw new RuntimeException(e);
		}
	}

}
