package org.gotti.wurmunlimited.modsupport.creatures;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.wurmonline.server.zones.Encounter;
import com.wurmonline.server.zones.EncounterType;
import com.wurmonline.server.zones.SpawnTable;

public class EncounterBuilder {

	private byte tiletype;
	private byte elevation;
	private Encounter encounter;
	
	private static Method spawnTableAddTileType;
	
	static {
		try {
			spawnTableAddTileType = SpawnTable.class.getDeclaredMethod("addTileType", EncounterType.class);
			spawnTableAddTileType.setAccessible(true);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	public EncounterBuilder(byte tiletype) {
		this(tiletype, (byte) 0);
	}

	public EncounterBuilder(byte tiletype, byte elevation) {
		this.tiletype = tiletype;
		this.elevation = elevation;
		
		this.encounter = new Encounter();
	}
	
	public EncounterBuilder addCreatures(int templateId, int count) {
		encounter.addType(templateId, count);
		return this;
	}

	public void build(int chance) {
		if (encounter == null || chance == 0) {
			return;
		}

		try {
			EncounterType encounterType = SpawnTable.getType(tiletype, elevation);
			if (encounterType == null) {
				encounterType = new EncounterType(tiletype, elevation);
				spawnTableAddTileType.invoke(SpawnTable.class, encounterType);
			}
			encounterType.addEncounter(encounter, chance);
		} catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
			throw new RuntimeException(e);
		}
	}

}
