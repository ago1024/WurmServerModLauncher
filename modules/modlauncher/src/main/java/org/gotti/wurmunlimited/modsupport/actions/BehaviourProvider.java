package org.gotti.wurmunlimited.modsupport.actions;

import java.util.List;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.bodys.Wound;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.structures.BridgePart;
import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.structures.Floor;
import com.wurmonline.server.structures.Wall;

public interface BehaviourProvider {

	public default List<ActionEntry> getBehavioursFor(Creature performer, long target) {
		return null;
	}

	public default List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, int tile) {
		return null;
	}

	public default List<ActionEntry> getBehavioursFor(Creature performer, int tilex, int tiley, boolean onSurface, int tile) {
		return null;
	}

	public default List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, int tile, int dir) {
		return null;
	}

	public default List<ActionEntry> getBehavioursFor(Creature performer, int tilex, int tiley, boolean onSurface, int tile, int dir) {
		return null;
	}

	public default List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, Tiles.TileBorderDirection dir, boolean border, int heightOffset) {
		return null;
	}

	public default List<ActionEntry> getBehavioursFor(Creature performer, int tilex, int tiley, boolean onSurface, Tiles.TileBorderDirection dir, boolean border, int heightOffset) {
		return null;
	}

	@Deprecated
	public default List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, boolean corner, int tile) {
		return null;
	}

	public default List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, boolean corner, int tile, int heightOffset) {
		return getBehavioursFor(performer, object, tilex, tiley, onSurface, corner, tile);
	}

	@Deprecated
	public default List<ActionEntry> getBehavioursFor(Creature performer, int tilex, int tiley, boolean onSurface, boolean corner, int tile) {
		return null;
	}

	public default List<ActionEntry> getBehavioursFor(Creature performer, int tilex, int tiley, boolean onSurface, boolean corner, int tile, int heightOffset) {
		return getBehavioursFor(performer, tilex, tiley, onSurface, corner, tile);
	}

	public default List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Skill skill) {
		return null;
	}

	public default List<ActionEntry> getBehavioursFor(Creature performer, Skill skill) {
		return null;
	}

	public default List<ActionEntry> getBehavioursFor(Creature performer, Item target) {
		return null;
	}

	public default List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Item target) {
		return null;
	}

	public default List<ActionEntry> getBehavioursFor(Creature performer, Wound target) {
		return null;
	}

	public default List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Wound target) {
		return null;
	}

	public default List<ActionEntry> getBehavioursFor(Creature performer, Creature target) {
		return null;
	}

	public default List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Creature target) {
		return null;
	}

	public default List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Wall target) {
		return null;
	}

	public default List<ActionEntry> getBehavioursFor(Creature performer, Wall target) {
		return null;
	}

	public default List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Fence target) {
		return null;
	}

	public default List<ActionEntry> getBehavioursFor(Creature performer, Fence target) {
		return null;
	}

	public default List<ActionEntry> getBehavioursFor(Creature performer, Item object, int planetId) {
		return null;
	}

	public default List<ActionEntry> getBehavioursFor(Creature performer, int planetId) {
		return null;
	}

	public default List<ActionEntry> getBehavioursFor(Creature performer, boolean onSurface, Floor floor) {
		return null;
	}

	public default List<ActionEntry> getBehavioursFor(Creature creature, Item item, boolean onSurface, Floor floor) {
		return null;
	}

	public default List<ActionEntry> getBehavioursFor(Creature aPerformer, boolean aOnSurface, BridgePart aBridgePart) {
		return null;
	}

	public default List<ActionEntry> getBehavioursFor(Creature aPerformer, Item item, boolean aOnSurface, BridgePart aBridgePart) {
		return null;
	}
}
