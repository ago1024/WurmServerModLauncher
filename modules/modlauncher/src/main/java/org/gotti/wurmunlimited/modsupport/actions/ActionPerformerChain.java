package org.gotti.wurmunlimited.modsupport.actions;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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

class ActionPerformerChain implements ActionPerformerBase {
	
	private short actionId;
	
	private List<ActionPerformer> actionPerformers = new CopyOnWriteArrayList<>();

	public ActionPerformerChain(short actionId) {
		this.actionId = actionId;
	}

	@Override
	public short getActionId() {
		return actionId;
	}
	
	public void addActionPerformer(ActionPerformer actionPerformer) {
		if (actionPerformer.getActionId() != getActionId()) {
			throw new IllegalArgumentException("ActionId does not match actionId of ActionPerformerChain");
		}
		
		actionPerformers.add(actionPerformer);
	}
	
	public boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, boolean corner, int tile, int heightOffset, short num, float counter) {
		return wrap(action).action(action, performer, source, tilex, tiley, onSurface, corner, tile, heightOffset, num, counter);
	}

	public boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, boolean corner, int tile, int heightOffset, short num, float counter) {
		return wrap(action).action(action, performer, tilex, tiley, onSurface, corner, tile, heightOffset, num, counter);
	}

	public boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, int tile, short num, float counter) {
		return wrap(action).action(action, performer, tilex, tiley, onSurface, tile, num, counter);
	}

	public boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, int heightOffset, int tile, short num, float counter) {
		return wrap(action).action(action, performer, source, tilex, tiley, onSurface, heightOffset, tile, num, counter);
	}

	public boolean action(Action action, Creature performer, int planetId, short num, float counter) {
		return wrap(action).action(action, performer, planetId, num, counter);
	}

	public boolean action(Action action, Creature performer, Item source, int planetId, short num, float counter) {
		return wrap(action).action(action, performer, source, planetId, num, counter);
	}

	public boolean action(Action action, Creature performer, Item source, Item target, short num, float counter) {
		return wrap(action).action(action, performer, source, target, num, counter);
	}

	public boolean action(Action action, Creature performer, Wound target, short num, float counter) {
		return wrap(action).action(action, performer, target, num, counter);
	}

	public boolean action(Action action, Creature performer, Item source, Wound target, short num, float counter) {
		return wrap(action).action(action, performer, source, target, num, counter);
	}

	public boolean action(Action action, Creature performer, Item target, short num, float counter) {
		return wrap(action).action(action, performer, target, num, counter);
	}

	public boolean action(Action action, Creature performer, Item source, Creature target, short num, float counter) {
		return wrap(action).action(action, performer, source, target, num, counter);
	}

	public boolean action(Action action, Creature performer, Creature target, short num, float counter) {
		return wrap(action).action(action, performer, target, num, counter);
	}

	public boolean action(Action action, Creature performer, Item source, Wall target, short num, float counter) {
		return wrap(action).action(action, performer, source, target, num, counter);
	}

	public boolean action(Action action, Creature performer, Wall target, short num, float counter) {
		return wrap(action).action(action, performer, target, num, counter);
	}

	public boolean action(Action action, Creature performer, Item source, boolean onSurface, Fence target, short num, float counter) {
		return wrap(action).action(action, performer, source, onSurface, target, num, counter);
	}

	public boolean action(Action action, Creature performer, boolean onSurface, Fence target, short num, float counter) {
		return wrap(action).action(action, performer, onSurface, target, num, counter);
	}

	public boolean action(Action action, Creature performer, Item source, Skill skill, short num, float counter) {
		return wrap(action).action(action, performer, source, skill, num, counter);
	}

	public boolean action(Action action, Creature performer, Skill skill, short num, float counter) {
		return wrap(action).action(action, performer, skill, num, counter);
	}

	public boolean action(Action action, Creature performer, Item source, boolean onSurface, Floor target, int encodedTile, short num, float counter) {
		return wrap(action).action(action, performer, source, onSurface, target, encodedTile, num, counter);
	}

	public boolean action(Action action, Creature performer, boolean onSurface, Floor floor, int encodedTile, short num, float counter) {
		return wrap(action).action(action, performer, onSurface, floor, encodedTile, num, counter);
	}

	public boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, int heightOffset, Tiles.TileBorderDirection dir, long borderId, short num, float counter) {
		return wrap(action).action(action, performer, source, tilex, tiley, onSurface, heightOffset, dir, borderId, num, counter);
	}

	public boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, Tiles.TileBorderDirection dir, long borderId, short num, float counter) {
		return wrap(action).action(action, performer, tilex, tiley, onSurface, dir, borderId, num, counter);
	}

	public boolean action(Action action, Creature performer, Item[] targets, short num, float counter) {
		return wrap(action).action(action, performer, targets, num, counter);
	}

	public boolean action(Action action, Creature performer, boolean onSurface, BridgePart bridgePart, int encodedTile, short num, float counter) {
		return wrap(action).action(action, performer, onSurface, bridgePart, encodedTile, num, counter);
	}

	public boolean action(Action action, Creature performer, Item item, boolean onSurface, BridgePart bridgePart, int encodedTile, short num, float counter) {
		return wrap(action).action(action, performer, item, onSurface, bridgePart, encodedTile, num, counter);
	}
	
	@Override
	public boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, boolean corner, int tile, short num, float counter) {
		return wrap(action).action(action, performer, source, tilex, tiley, onSurface, corner, tile, 0, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, boolean corner, int tile, short num, float counter) {
		return wrap(action).action(action, performer, tilex, tiley, onSurface, corner, tile, 0, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, int tile, int dir, short num, float counter) {
		return wrap(action).action(action, performer, tilex, tiley, onSurface, tile, dir, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, int heightOffset, int tile, int dir, short num, float counter) {
		return wrap(action).action(action, performer, source, tilex, tiley, onSurface, heightOffset, tile, dir, num, counter);
	}

	private WrappedBehaviour wrap(Action action) {
		return new WrappedBehaviour(action.getBehaviour(), WrappedBehaviour.getDefaultActionReturnValue(action), actionPerformers);
	}

}
