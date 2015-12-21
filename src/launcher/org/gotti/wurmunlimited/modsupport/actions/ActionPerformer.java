package org.gotti.wurmunlimited.modsupport.actions;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.bodys.Wound;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.structures.BridgePart;
import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.structures.Floor;
import com.wurmonline.server.structures.Wall;

public interface ActionPerformer {

	public default boolean action(Action act, Creature performer, Item source, int tilex, int tiley, boolean onSurface, boolean corner, int tile, short action, float counter) {
		return true;
	}

	public default boolean action(Action act, Creature performer, int tilex, int tiley, boolean onSurface, boolean corner, int tile, short action, float counter) {
		return true;
	}

	public default boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, int tile, short num, float counter) {
		return true;
	}

	public default boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, int heightOffset, int tile, short num, float counter) {
		return true;
	}

	public default boolean action(Action action, Creature performer, int planetId, short num, float counter) {
		return true;
	}

	public default boolean action(Action action, Creature performer, Item source, int planetId, short num, float counter) {
		return true;
	}

	public default boolean action(Action action, Creature performer, Item source, Item target, short num, float counter) {
		return true;
	}

	public default boolean action(Action action, Creature performer, Wound target, short num, float counter) {
		return true;
	}

	public default boolean action(Action action, Creature performer, Item source, Wound target, short num, float counter) {
		return true;
	}

	public default boolean action(Action action, Creature performer, Item target, short num, float counter) {
		return true;
	}

	public default boolean action(Action action, Creature performer, Item source, Creature target, short num, float counter) {
		return true;
	}

	public default boolean action(Action action, Creature performer, Creature target, short num, float counter) {
		return true;
	}

	public default boolean action(Action action, Creature performer, Item source, Wall target, short num, float counter) {
		return true;
	}

	public default boolean action(Action action, Creature performer, Wall target, short num, float counter) {
		return true;
	}

	public default boolean action(Action action, Creature performer, Item source, boolean onSurface, Fence target, short num, float counter) {
		return true;
	}

	public default boolean action(Action action, Creature performer, boolean onSurface, Fence target, short num, float counter) {
		return true;
	}

	public default boolean action(Action act, Creature performer, Item source, Skill skill, short action, float counter) {
		return true;
	}

	public default boolean action(Action act, Creature performer, Skill skill, short action, float counter) {
		return true;
	}

	public default boolean action(Action action, Creature performer, Item source, boolean onSurface, Floor target, int encodedTile, short num, float counter) {
		return true;
	}

	public default boolean action(Action act, Creature performer, boolean onSurface, Floor floor, int encodedTile, short action, float counter) {
		return true;
	}

	public default boolean action(Action aAct, Creature aPerformer, Item aSource, int aTilex, int aTiley, boolean onSurface, int aHeightOffset, Tiles.TileBorderDirection aDir, long borderId, short aAction, float aCounter) {
		return true;
	}

	public default boolean action(Action aAct, Creature aPerformer, int aTilex, int aTiley, boolean onSurface, Tiles.TileBorderDirection aDir, long borderId, short aAction, float aCounter) {
		return true;
	}

	public default boolean action(Action action, Creature performer, Item[] targets, short num, float counter) {
		return true;
	}

	public default boolean action(Action act, Creature performer, boolean onSurface, BridgePart aBridgePart, int encodedTile, short action, float counter) {
		return true;
	}

	public default boolean action(Action act, Creature performer, Item item, boolean onSurface, BridgePart aBridgePart, int encodedTile, short action, float counter) {
		return true;
	}

	public short getActionId();

}
