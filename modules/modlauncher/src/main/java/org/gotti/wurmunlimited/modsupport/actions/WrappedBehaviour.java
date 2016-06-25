package org.gotti.wurmunlimited.modsupport.actions;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Predicate;

import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.HookException;

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

class WrappedBehaviour extends Behaviour {
	
	private boolean serverPropagation;
	private Behaviour behaviour;
	private List<ActionPerformer> actionPerformers;
	
	private static Field fBehaviour;
	static {
		try {
			fBehaviour = ReflectionUtil.getField(Action.class, "behaviour");
		} catch (NoSuchFieldException e) {
			throw new HookException(e);
		}
	}

	public WrappedBehaviour(Behaviour behaviour, List<ActionPerformer> actionPerformers) {
		this.serverPropagation = true;
		this.behaviour = behaviour;
		this.actionPerformers = actionPerformers;
	}
	
	public boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, boolean corner, int tile, short num, float counter) {
		return action(action, actionPerformer -> actionPerformer.action(action, performer, source, tilex, tiley, onSurface, corner, tile, num, counter));
	}

	public boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, boolean corner, int tile, short num, float counter) {
		return action(action, actionPerformer -> actionPerformer.action(action, performer, tilex, tiley, onSurface, corner, tile, num, counter));
	}

	public boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, int tile, short num, float counter) {
		return action(action, actionPerformer -> actionPerformer.action(action, performer, tilex, tiley, onSurface, tile, num, counter));
	}

	public boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, int heightOffset, int tile, short num, float counter) {
		return action(action, actionPerformer -> actionPerformer.action(action, performer, source, tilex, tiley, onSurface, heightOffset, tile, num, counter));
	}

	public boolean action(Action action, Creature performer, int planetId, short num, float counter) {
		return action(action, actionPerformer -> actionPerformer.action(action, performer, planetId, num, counter));
	}

	public boolean action(Action action, Creature performer, Item source, int planetId, short num, float counter) {
		return action(action, actionPerformer -> actionPerformer.action(action, performer, source, planetId, num, counter));
	}

	public boolean action(Action action, Creature performer, Item source, Item target, short num, float counter) {
		return action(action, actionPerformer -> actionPerformer.action(action, performer, source, target, num, counter));
	}

	public boolean action(Action action, Creature performer, Wound target, short num, float counter) {
		return action(action, actionPerformer -> actionPerformer.action(action, performer, target, num, counter));
	}

	public boolean action(Action action, Creature performer, Item source, Wound target, short num, float counter) {
		return action(action, actionPerformer -> actionPerformer.action(action, performer, source, target, num, counter));
	}

	public boolean action(Action action, Creature performer, Item target, short num, float counter) {
		return action(action, actionPerformer -> actionPerformer.action(action, performer, target, num, counter));
	}

	public boolean action(Action action, Creature performer, Item source, Creature target, short num, float counter) {
		return action(action, actionPerformer -> actionPerformer.action(action, performer, source, target, num, counter));
	}

	public boolean action(Action action, Creature performer, Creature target, short num, float counter) {
		return action(action, actionPerformer -> actionPerformer.action(action, performer, target, num, counter));
	}

	public boolean action(Action action, Creature performer, Item source, Wall target, short num, float counter) {
		return action(action, actionPerformer -> actionPerformer.action(action, performer, source, target, num, counter));
	}

	public boolean action(Action action, Creature performer, Wall target, short num, float counter) {
		return action(action, actionPerformer -> actionPerformer.action(action, performer, target, num, counter));
	}

	public boolean action(Action action, Creature performer, Item source, boolean onSurface, Fence target, short num, float counter) {
		return action(action, actionPerformer -> actionPerformer.action(action, performer, source, onSurface, target, num, counter));
	}

	public boolean action(Action action, Creature performer, boolean onSurface, Fence target, short num, float counter) {
		return action(action, actionPerformer -> actionPerformer.action(action, performer, onSurface, target, num, counter));
	}

	public boolean action(Action action, Creature performer, Item source, Skill skill, short num, float counter) {
		return action(action, actionPerformer -> actionPerformer.action(action, performer, source, skill, num, counter));
	}

	public boolean action(Action action, Creature performer, Skill skill, short num, float counter) {
		return action(action, actionPerformer -> actionPerformer.action(action, performer, skill, num, counter));
	}

	public boolean action(Action action, Creature performer, Item source, boolean onSurface, Floor target, int encodedTile, short num, float counter) {
		return action(action, actionPerformer -> actionPerformer.action(action, performer, source, onSurface, target, encodedTile, num, counter));
	}

	public boolean action(Action action, Creature performer, boolean onSurface, Floor floor, int encodedTile, short num, float counter) {
		return action(action, actionPerformer -> actionPerformer.action(action, performer, onSurface, floor, encodedTile, num, counter));
	}

	public boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, int heightOffset, Tiles.TileBorderDirection dir, long borderId, short num, float counter) {
		return action(action, actionPerformer -> actionPerformer.action(action, performer, source, tilex, tiley, onSurface, heightOffset, dir, borderId, num, counter));
	}

	public boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, Tiles.TileBorderDirection dir, long borderId, short num, float counter) {
		return action(action, actionPerformer -> actionPerformer.action(action, performer, tilex, tiley, onSurface, dir, borderId, num, counter));
	}

	public boolean action(Action action, Creature performer, Item[] targets, short num, float counter) {
		return action(action, actionPerformer -> actionPerformer.action(action, performer, targets, num, counter));
	}

	public boolean action(Action action, Creature performer, boolean onSurface, BridgePart bridgePart, int encodedTile, short num, float counter) {
		return action(action, actionPerformer -> actionPerformer.action(action, performer, onSurface, bridgePart, encodedTile, num, counter));
	}

	public boolean action(Action action, Creature performer, Item item, boolean onSurface, BridgePart bridgePart, int encodedTile, short num, float counter) {
		return action(action, actionPerformer -> actionPerformer.action(action, performer, item, onSurface, bridgePart, encodedTile, num, counter));
	}

	private boolean action(Action action, Predicate<Behaviour> code) {
		if (!isServerBehaviour()) {
			Behaviour actionBehaviour = action.getBehaviour();
			try {
				setActionBehaviour(action, new WrappedBehaviour(behaviour, actionPerformers.subList(1, actionPerformers.size())));
				return code.test(new ActionPerformerBehaviour(actionPerformers.get(0)));
			} finally {
				setActionBehaviour(action, actionBehaviour);
			}
		} else if (isServerPropagation()) {
			// Propagate the action to the server Behavior classes
			return code.test(this.behaviour);
		} else {
			// Don't propagate the action
			return true;
		}
	}

	private void setActionBehaviour(Action action, Behaviour behaviour) {
		try {
			ReflectionUtil.setPrivateField(action, fBehaviour, behaviour);
		} catch (IllegalAccessException e) {
			throw new HookException(e);
		}
	}
	
	public Behaviour getBehaviour() {
		return behaviour;
	}
	
	public boolean isServerBehaviour() {
		return actionPerformers.isEmpty();
	}
	
	public boolean isServerPropagation() {
		return serverPropagation;
	}
	
	public void setServerPropagation(boolean propagation) {
		serverPropagation = propagation;
	}
	
	public static Behaviour unwrapBehaviour(Behaviour behaviour) {
		if (behaviour instanceof WrappedBehaviour) {
			return ((WrappedBehaviour)behaviour).getBehaviour();
		}
		return behaviour;
	}
	
	public static boolean isWrapped(Behaviour behaviour) {
		if (behaviour instanceof WrappedBehaviour) {
			return !((WrappedBehaviour)behaviour).isServerBehaviour();
		}
		return false;
	}

	public static void setServerPropagation(Behaviour behaviour, boolean propagate) {
		if (behaviour instanceof WrappedBehaviour) {
			((WrappedBehaviour)behaviour).setServerPropagation(propagate);
		}
	}
}
