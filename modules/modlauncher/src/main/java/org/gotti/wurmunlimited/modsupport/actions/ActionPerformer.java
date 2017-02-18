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

/**
 * Interface to abstract performing an action from the Behaviour classes.
 * 
 * The interface defines all action() methods of Behaviour. The ModActions hook will call a registered ActionPerformer instead of the server defined Behaviour classes.
 * 
 * The ActionPerformer should call the super implementations (ActionPerformer.super.action(...)) to allow chaining multiple ActionPerformers and to forward the action to the original implementation.
 *
 */
public interface ActionPerformer extends ActionPerformerBase {

	public default boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, boolean corner, int tile, short num, float counter) {
		setServerPropagation(action, true);
		return true;
	}

	public default boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, boolean corner, int tile, short num, float counter) {
		setServerPropagation(action, true);
		return true;
	}

	public default boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, int tile, short num, float counter) {
		setServerPropagation(action, true);
		return true;
	}

	public default boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, int heightOffset, int tile, short num, float counter) {
		setServerPropagation(action, true);
		return true;
	}

	public default boolean action(Action action, Creature performer, int planetId, short num, float counter) {
		setServerPropagation(action, true);
		return true;
	}

	public default boolean action(Action action, Creature performer, Item source, int planetId, short num, float counter) {
		setServerPropagation(action, true);
		return true;
	}

	public default boolean action(Action action, Creature performer, Item source, Item target, short num, float counter) {
		setServerPropagation(action, true);
		return true;
	}

	public default boolean action(Action action, Creature performer, Wound target, short num, float counter) {
		setServerPropagation(action, true);
		return true;
	}

	public default boolean action(Action action, Creature performer, Item source, Wound target, short num, float counter) {
		setServerPropagation(action, true);
		return true;
	}

	public default boolean action(Action action, Creature performer, Item target, short num, float counter) {
		setServerPropagation(action, true);
		return true;
	}

	public default boolean action(Action action, Creature performer, Item source, Creature target, short num, float counter) {
		setServerPropagation(action, true);
		return true;
	}

	public default boolean action(Action action, Creature performer, Creature target, short num, float counter) {
		setServerPropagation(action, true);
		return true;
	}

	public default boolean action(Action action, Creature performer, Item source, Wall target, short num, float counter) {
		setServerPropagation(action, true);
		return true;
	}

	public default boolean action(Action action, Creature performer, Wall target, short num, float counter) {
		setServerPropagation(action, true);
		return true;
	}

	public default boolean action(Action action, Creature performer, Item source, boolean onSurface, Fence target, short num, float counter) {
		setServerPropagation(action, true);
		return true;
	}

	public default boolean action(Action action, Creature performer, boolean onSurface, Fence target, short num, float counter) {
		setServerPropagation(action, true);
		return true;
	}

	public default boolean action(Action action, Creature performer, Item source, Skill skill, short num, float counter) {
		setServerPropagation(action, true);
		return true;
	}

	public default boolean action(Action action, Creature performer, Skill skill, short num, float counter) {
		setServerPropagation(action, true);
		return true;
	}

	public default boolean action(Action action, Creature performer, Item source, boolean onSurface, Floor target, int encodedTile, short num, float counter) {
		setServerPropagation(action, true);
		return true;
	}

	public default boolean action(Action action, Creature performer, boolean onSurface, Floor floor, int encodedTile, short num, float counter) {
		setServerPropagation(action, true);
		return true;
	}

	public default boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, int heightOffset, Tiles.TileBorderDirection dir, long borderId, short num, float counter) {
		setServerPropagation(action, true);
		return true;
	}

	public default boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, Tiles.TileBorderDirection dir, long borderId, short num, float counter) {
		setServerPropagation(action, true);
		return true;
	}

	public default boolean action(Action action, Creature performer, Item[] targets, short num, float counter) {
		setServerPropagation(action, true);
		return true;
	}

	public default boolean action(Action action, Creature performer, boolean onSurface, BridgePart bridgePart, int encodedTile, short num, float counter) {
		setServerPropagation(action, true);
		return true;
	}

	public default boolean action(Action action, Creature performer, Item item, boolean onSurface, BridgePart bridgePart, int encodedTile, short num, float counter) {
		setServerPropagation(action, true);
		return true;
	}

	/**
	 * Get the action if which is handled by this ActionPerformer.
	 */
	public short getActionId();

	/**
	 * Check if the action would directly call a servers Behaviour object. 
	 * 
	 * @param action Action
	 * @return true if the behaviour object not wrapped or directly wraps a servers Behaviour object
	 */
	public static boolean isServerBehaviour(Action action) {
		return WrappedBehaviour.isServerBehaviour(action.getBehaviour());
	}

	/**
	 * Get the servers Behaviour object which would be called at the end
	 * of the ActionPerformer chain.
	 * 
	 * @param action Action
	 * @return Behaviour object
	 */
	public static Behaviour getServerBehaviour(Action action) {
		return WrappedBehaviour.unwrapBehaviour(action.getBehaviour());
	}
	
	/**
	 * Prevent (or enable) the ActionPerformer chain from calling the servers Behaviour object after the chain has been processed.
	 * @param action Action
	 * @param propagate true if the chain should call the servers Behaviour object, false if it should stop after the last ActionPerformer
	 */
	public static void setServerPropagation(Action action, boolean propagate) {
		WrappedBehaviour.setServerPropagation(action.getBehaviour(), propagate);
	}
}
