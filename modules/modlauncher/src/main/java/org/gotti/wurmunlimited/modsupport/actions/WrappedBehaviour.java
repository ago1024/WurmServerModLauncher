package org.gotti.wurmunlimited.modsupport.actions;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

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

/**
 * Calls multiple {@link ActionPerformer}s and the original {@link Behaviour} unless prevented by setServerPropagation(action, false).
 * <p>
 * The {@link WrappedBehaviour} is called by the {@link ActionPerformerChain}.
 * 
 */
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
	
	public boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, boolean corner, int tile, int heightOffset, short num, float counter) {
		return action(action, actionPerformer -> actionPerformer.action(action, performer, source, tilex, tiley, onSurface, corner, tile, heightOffset, num, counter));
	}

	public boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, boolean corner, int tile, int heightOffset, short num, float counter) {
		return action(action, actionPerformer -> actionPerformer.action(action, performer, tilex, tiley, onSurface, corner, tile, heightOffset, num, counter));
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

	/**
	 * Call the ActionPerformer
	 * @param action Action
	 * @param code Lambda with call to correct action method on the ActionPerformer
	 * @return true if the action is done, false if it should continue
	 */
	private boolean action(Action action, Predicate<Behaviour> code) {
		boolean result = false;
		boolean propagate = this.serverPropagation;
		for (ActionPerformer actionPerformer : actionPerformers) {
			Behaviour actionBehaviour = action.getBehaviour();
			try {
				// Create a behaviour without any ActionPerformes. This behaviour will call the server action
				// if called recursively
				WrappedBehaviour wrapped = new WrappedBehaviour(actionBehaviour, Collections.emptyList());
				setActionBehaviour(action, wrapped);
				wrapped.serverPropagation = false;
				// Call the actionPerformer
				result |= code.test(new ActionPerformerBehaviour(actionPerformer));
				// Check if serverPropation was reset to true. This is done in the ActionPerformer default methods
				propagate &= wrapped.serverPropagation;
			} catch (Exception e) {
				// Log the error and remove the faulty action performer
				Logger.getLogger(WrappedBehaviour.class.getName()).log(Level.SEVERE, e.getMessage(), e);
				actionPerformers.remove(actionPerformer);
			} finally {
				setActionBehaviour(action, actionBehaviour);
			}
		}
		if (propagate) {
			// Propagate the action to the server Behavior classes
			return code.test(this.behaviour) || result;
		} else {
			// Don't propagate the action
			return result;
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
	
	public static boolean isServerBehaviour(Behaviour behaviour) {
		if (behaviour instanceof WrappedBehaviour) {
			return ((WrappedBehaviour)behaviour).actionPerformers.isEmpty();
		}
		return true;
	}

	public static void setServerPropagation(Behaviour behaviour, boolean propagate) {
		if (behaviour instanceof WrappedBehaviour) {
			((WrappedBehaviour)behaviour).setServerPropagation(propagate);
		}
	}
}
