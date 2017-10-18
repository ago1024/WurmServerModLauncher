package org.gotti.wurmunlimited.modsupport.actions;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.Behaviour;
import com.wurmonline.server.bodys.Wound;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.structures.BridgePart;
import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.structures.Floor;
import com.wurmonline.server.structures.Wall;

public class ActionPerformerBehaviour extends Behaviour implements ActionPerformerBase {

	private ActionPerformerBase actionPerformer;

	public ActionPerformerBehaviour(ActionPerformerBase actionPerformer) {
		this.actionPerformer = actionPerformer;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, boolean corner, int tile, short num, float counter) {
		return actionPerformer.action(action, performer, source, tilex, tiley, onSurface, corner, tile, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, boolean corner, int tile, int heightOffset, short num, float counter) {
		return actionPerformer.action(action, performer, source, tilex, tiley, onSurface, corner, tile, heightOffset, num, counter);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, boolean corner, int tile, short num, float counter) {
		return actionPerformer.action(action, performer, tilex, tiley, onSurface, corner, tile, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, boolean corner, int tile, int heightOffset, short num, float counter) {
		return actionPerformer.action(action, performer, tilex, tiley, onSurface, corner, tile, heightOffset, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, int tile, short num, float counter) {
		return actionPerformer.action(action, performer, tilex, tiley, onSurface, tile, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, int heightOffset, int tile, short num, float counter) {
		return actionPerformer.action(action, performer, source, tilex, tiley, onSurface, heightOffset, tile, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, int planetId, short num, float counter) {
		return actionPerformer.action(action, performer, planetId, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, Item source, int planetId, short num, float counter) {
		return actionPerformer.action(action, performer, source, planetId, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, Item source, Item target, short num, float counter) {
		return actionPerformer.action(action, performer, source, target, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, Wound target, short num, float counter) {
		return actionPerformer.action(action, performer, target, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, Item source, Wound target, short num, float counter) {
		return actionPerformer.action(action, performer, source, target, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, Item target, short num, float counter) {
		return actionPerformer.action(action, performer, target, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, Item source, Creature target, short num, float counter) {
		return actionPerformer.action(action, performer, source, target, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, Creature target, short num, float counter) {
		return actionPerformer.action(action, performer, target, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, Item source, Wall target, short num, float counter) {
		return actionPerformer.action(action, performer, source, target, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, Wall target, short num, float counter) {
		return actionPerformer.action(action, performer, target, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, Item source, boolean onSurface, Fence target, short num, float counter) {
		return actionPerformer.action(action, performer, source, onSurface, target, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, boolean onSurface, Fence target, short num, float counter) {
		return actionPerformer.action(action, performer, onSurface, target, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, Item source, Skill skill, short num, float counter) {
		return actionPerformer.action(action, performer, source, skill, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, Skill skill, short num, float counter) {
		return actionPerformer.action(action, performer, skill, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, Item source, boolean onSurface, Floor target, int encodedTile, short num, float counter) {
		return actionPerformer.action(action, performer, source, onSurface, target, encodedTile, num, counter);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean action(Action action, Creature performer, boolean onSurface, Floor floor, int encodedTile, short num, float counter) {
		return actionPerformer.action(action, performer, onSurface, floor, encodedTile, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, int heightOffset, Tiles.TileBorderDirection dir, long borderId, short num, float counter) {
		return actionPerformer.action(action, performer, source, tilex, tiley, onSurface, heightOffset, dir, borderId, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, Tiles.TileBorderDirection dir, long borderId, short num, float counter) {
		return actionPerformer.action(action, performer, tilex, tiley, onSurface, dir, borderId, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, Item[] targets, short num, float counter) {
		return actionPerformer.action(action, performer, targets, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, boolean onSurface, BridgePart bridgePart, int encodedTile, short num, float counter) {
		return actionPerformer.action(action, performer, onSurface, bridgePart, encodedTile, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, Item item, boolean onSurface, BridgePart bridgePart, int encodedTile, short num, float counter) {
		return actionPerformer.action(action, performer, item, onSurface, bridgePart, encodedTile, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, int tile, int dir, short num, final float counter) {
		return actionPerformer.action(action, performer, tilex, tiley, onSurface, tile, dir, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, int heightOffset, int tile, int dir, short num, float counter) {
		return actionPerformer.action(action, performer, source, tilex, tiley, onSurface, heightOffset, tile, dir, num, counter);
	}

	@Override
	public short getActionId() {
		return actionPerformer.getActionId();
	}
}
