package org.gotti.wurmunlimited.modsupport.actions;

import java.util.List;

import com.wurmonline.mesh.Tiles.TileBorderDirection;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.Behaviour;
import com.wurmonline.server.bodys.Wound;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.structures.BridgePart;
import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.structures.Floor;
import com.wurmonline.server.structures.Wall;

public class WrappedBehaviourProvider implements BehaviourProvider {
	private Behaviour wrapped;

	public WrappedBehaviourProvider(Behaviour wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature aPerformer, boolean aOnSurface, BridgePart aBridgePart) {
		return wrapped.getBehavioursFor(aPerformer, aOnSurface, aBridgePart);
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature aPerformer, Item item, boolean aOnSurface, BridgePart aBridgePart) {
		return wrapped.getBehavioursFor(aPerformer, item, aOnSurface, aBridgePart);
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature creature, Item item, boolean onSurface, Floor floor) {
		return wrapped.getBehavioursFor(creature, item, onSurface, floor);
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, boolean onSurface, Floor floor) {
		return wrapped.getBehavioursFor(performer, onSurface, floor);
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Creature target) {
		return wrapped.getBehavioursFor(performer, target);
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Fence target) {
		return wrapped.getBehavioursFor(performer, target);
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, int planetId) {
		return wrapped.getBehavioursFor(performer, planetId);
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, int tilex, int tiley, boolean onSurface, boolean corner, int tile, int heightOffset) {
		return wrapped.getBehavioursFor(performer, tilex, tiley, onSurface, corner, tile, heightOffset);
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, int tilex, int tiley, boolean onSurface, int tile) {
		return wrapped.getBehavioursFor(performer, tilex, tiley, onSurface, tile);
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, int tilex, int tiley, boolean onSurface, int tile, int dir) {
		return wrapped.getBehavioursFor(performer, tilex, tiley, onSurface, tile, dir);
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, int tilex, int tiley, boolean onSurface, TileBorderDirection dir, boolean border, int heightOffset) {
		return wrapped.getBehavioursFor(performer, tilex, tiley, onSurface, dir, border, heightOffset);
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item object, int planetId) {
		return wrapped.getBehavioursFor(performer, object, planetId);
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, boolean corner, int tile, int heightOffset) {
		return wrapped.getBehavioursFor(performer, object, tilex, tiley, onSurface, corner, tile, heightOffset);
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, int tile) {
		return wrapped.getBehavioursFor(performer, object, tilex, tiley, onSurface, tile);
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, int tile, int dir) {
		return wrapped.getBehavioursFor(performer, object, tilex, tiley, onSurface, tile, dir);
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, TileBorderDirection dir, boolean border, int heightOffset) {
		return wrapped.getBehavioursFor(performer, object, tilex, tiley, onSurface, dir, border, heightOffset);
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Creature target) {
		return wrapped.getBehavioursFor(performer, subject, target);
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Fence target) {
		return wrapped.getBehavioursFor(performer, subject, target);
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Item target) {
		return wrapped.getBehavioursFor(performer, subject, target);
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Skill skill) {
		return wrapped.getBehavioursFor(performer, subject, skill);
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Wall target) {
		return wrapped.getBehavioursFor(performer, subject, target);
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Wound target) {
		return wrapped.getBehavioursFor(performer, subject, target);
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item target) {
		return wrapped.getBehavioursFor(performer, target);
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, long target) {
		return wrapped.getBehavioursFor(performer, target);
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Skill skill) {
		return wrapped.getBehavioursFor(performer, skill);
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Wall target) {
		return wrapped.getBehavioursFor(performer, target);
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Wound target) {
		return wrapped.getBehavioursFor(performer, target);
	}

}
