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
	
	/** Flag if the action should be propagated to the server */
	private boolean serverPropagation;
	/** Flag if the action should be propagated to the next action performers */
	private boolean actionPerformerPropagation;
	/** Default return value for action() methods */
	private boolean actionReturnValue;
	
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
	
	public WrappedBehaviour(Behaviour behaviour, boolean defaultActionReturnValue, List<ActionPerformer> actionPerformers) {
		this.serverPropagation = true;
		this.actionPerformerPropagation = true;
		this.actionReturnValue = defaultActionReturnValue;
		this.behaviour = behaviour;
		this.actionPerformers = actionPerformers;
	}

	@Deprecated
	public WrappedBehaviour(Behaviour behaviour, List<ActionPerformer> actionPerformers) {
		this(behaviour, true, actionPerformers);
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

	public boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, int tile, int dir, short num, float counter) {
		return action(action, actionPerformer -> actionPerformer.action(action, performer, tilex, tiley, onSurface, tile, dir, num, counter));
	}

	public boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, int heightOffset, int tile, int dir, short num, float counter) {
		return action(action, actionPerformer -> actionPerformer.action(action, performer, source, tilex, tiley, onSurface, heightOffset, tile, dir, num, counter));
	}

	/**
	 * Get the default action() method return value. This is true (finish action) for any custom actions
	 * and false (continue action) for any server actions.
	 * 
	 * @param action
	 * @return
	 */
	public static boolean getDefaultActionReturnValue(Action action) {
		if (action.getNumber() > ModActions.getLastServerActionId()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Call the ActionPerformer
	 * @param action Action
	 * @param code Lambda with call to correct action method on the ActionPerformer
	 * @return true if the action is done, false if it should continue
	 */
	private boolean action(Action action, Predicate<Behaviour> code) {
		boolean actionResult = false;
		boolean propagateToServer = this.serverPropagation;

		final boolean defaultReturnValue = getDefaultActionReturnValue(action);
		final Behaviour actionBehaviour = action.getBehaviour();
		for (ActionPerformer actionPerformer : actionPerformers) {
			try {
				WrappedBehaviour wrapped = new WrappedBehaviour(actionBehaviour, defaultReturnValue, Collections.emptyList());
				setActionBehaviour(action, wrapped);

				// Set default server propagation to false. This was the default for the first version where
				// a direct return from the action() method would not call any other ActionPerformers or the
				// server behaviour
				wrapped.serverPropagation = false;

				// Call the actionPerformer
				final boolean result = code.test(new ActionPerformerBehaviour(actionPerformer));

				// Set the action() method return value. The action will be finished if any action performer wants to finish it.
				actionResult |= result;

				// Set the server propagation. The action will not propagate if any action performer wants to not propagate it.
				propagateToServer &= wrapped.serverPropagation;

				// If the action performer wants to stop propagation to other action performers then break out
				if (!wrapped.actionPerformerPropagation) {
					break;
				}
			} catch (Exception e) {
				// Log the error and remove the faulty action performer
				Logger.getLogger(WrappedBehaviour.class.getName()).log(Level.SEVERE, e.getMessage(), e);
				actionPerformers.remove(actionPerformer);
			} finally {
				setActionBehaviour(action, actionBehaviour);
			}
		}
		if (propagateToServer) {
			// Propagate the action to the server Behavior classes
			return code.test(this.behaviour) || actionResult;
		} else {
			// Don't propagate the action
			return actionResult;
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
	
	/**
	 * Set ActionPropagation flags.
	 * @param flags {@link ActionPropagation} false
	 * @return default return value for the action() method
	 */
	private boolean propagate(ActionPropagation... flags) {
		for (ActionPropagation flag : flags) {
			switch (flag) {
			case CONTINUE_ACTION:
				this.actionReturnValue = false;
				break;
			case FINISH_ACTION:
				this.actionReturnValue = true;
				break;
			case SERVER_PROPAGATION:
				this.serverPropagation = true;
				break;
			case NO_SERVER_PROPAGATION:
				this.serverPropagation = false;
				break;
			case ACTION_PERFORMER_PROPAGATION:
				this.actionPerformerPropagation = true;
				break;
			case NO_ACTION_PERFORMER_PROPAGATION:
				this.actionPerformerPropagation = false;
				break;
			}
		}
		return this.actionReturnValue;
	}
	
	/**
	 * Set ActionPropagation flags.
	 * @param behaviour Behaviour to set flags for
	 * @param flags {@link ActionPropagation} false
	 * @return default return value for the action() method
	 */
	public static boolean propagate(Behaviour behaviour, ActionPropagation... flags) {
		if (behaviour instanceof WrappedBehaviour) {
			return ((WrappedBehaviour)behaviour).propagate(flags);
		}
		return false;
	}
}
