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
import com.wurmonline.shared.constants.CounterTypes;

public interface ActionPerformerBase {

	short getActionId();

	//
	// Tile corners
	//
	/**
	 * Corner {@link CounterTypes#COUNTER_TYPE_TILECORNER} (27)
	 * Replaced with {@link ActionPerformer#action(Action, Creature, Item, int, int, boolean, boolean, int, int, short, float)
	 */
	@Deprecated
	boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, boolean corner, int tile, short num, float counter);

	/**
	 * Corner {@link CounterTypes#COUNTER_TYPE_TILECORNER} (27)
	 * <p>
	 * parameter "corner" is always true
	 * @since 1.4
	 */
	boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, boolean corner, int tile, int heightOffset, short num, float counter);

	/**
	 * Corner {@link CounterTypes#COUNTER_TYPE_TILECORNER} (27)
	 * Replaced with {@link ActionPerformer#action(Action, Creature, int, int, boolean, boolean, int, int, short, float)
	 */
	@Deprecated
	boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, boolean corner, int tile, short num, float counter);

	/**
	 * Corner {@link CounterTypes#COUNTER_TYPE_TILECORNER} (27)
	 * <p>
	 * parameter "corner" is always true
	 * @since 1.4
	 */
	boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, boolean corner, int tile, int heightOffset, short num, float counter);

	//
	// Tiles (surface)
	//
	/**
	 * Tiles {@link CounterTypes#COUNTER_TYPE_TILES} (3)
	 */
	boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, int tile, short num, float counter);

	/**
	 * Tiles {@link CounterTypes#COUNTER_TYPE_TILES} (3)
	 */
	boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, int heightOffset, int tile, short num, float counter);

	//
	// Planets, missions, tickets
	//
	/**
	 * Planets {@link CounterTypes#COUNTER_TYPE_PLANETS} (14)
	 * Mission {@link CounterTypes#COUNTER_TYPE_MISSIONPERFORMED} (22)
	 * Ticket {@link CounterTypes#COUNTER_TYPE_TICKETS} (25)
	 */
	boolean action(Action action, Creature performer, int id, short num, float counter);

	/**
	 * Planets {@link CounterTypes#COUNTER_TYPE_PLANETS} (14)
	 * Mission {@link CounterTypes#COUNTER_TYPE_MISSIONPERFORMED} (22)
	 */
	boolean action(Action action, Creature performer, Item source, int id, short num, float counter);

	//
	// Wounds
	//
	/**
	 * Wound {@link CounterTypes#COUNTER_TYPE_WOUNDS} (8)
	 */
	boolean action(Action action, Creature performer, Wound target, short num, float counter);

	/**
	 * Wound {@link CounterTypes#COUNTER_TYPE_WOUND} (8)
	 */
	boolean action(Action action, Creature performer, Item source, Wound target, short num, float counter);

	//
	// Items
	//
	/**
	 * Items {@link CounterTypes#COUNTER_TYPE_ITEMS} (2)
	 */
	boolean action(Action action, Creature performer, Item source, Item target, short num, float counter);

	/**
	 * Items {@link CounterTypes#COUNTER_TYPE_ITEMS} (2)
	 */
	boolean action(Action action, Creature performer, Item target, short num, float counter);

	/**
	 * Items {@link CounterTypes#COUNTER_TYPE_ITEMS} (2)
	 * Multi-action
	 */
	boolean action(Action action, Creature performer, Item[] targets, short num, float counter);

	//
	// Creatures
	//
	/**
	 * Creatures {@link CounterTypes#COUNTER_TYPE_CREATURES} (1)
	 */
	boolean action(Action action, Creature performer, Item source, Creature target, short num, float counter);

	/**
	 * Creatures {@link CounterTypes#COUNTER_TYPE_CREATURES} (1)
	 */
	boolean action(Action action, Creature performer, Creature target, short num, float counter);

	//
	// Walls
	//
	/**
	 * Walls {@link CounterTypes#COUNTER_TYPE_WALLS} (5)
	 */
	boolean action(Action action, Creature performer, Item source, Wall target, short num, float counter);

	/**
	 * Walls {@link CounterTypes#COUNTER_TYPE_WALLS} (5)
	 */
	boolean action(Action action, Creature performer, Wall target, short num, float counter);

	//
	// Fences
	//
	/**
	 * Fences {@link CounterTypes#COUNTER_TYPE_FENCES} (7)
	 */
	boolean action(Action action, Creature performer, Item source, boolean onSurface, Fence target, short num, float counter);

	/**
	 * Fences {@link CounterTypes#COUNTER_TYPE_FENCES} (7)
	 */
	boolean action(Action action, Creature performer, boolean onSurface, Fence target, short num, float counter);

	//
	// Skills
	//
	/**
	 * Skills {@link CounterTypes#COUNTER_TYPE_SKILLIDS} (18)
	 */
	boolean action(Action action, Creature performer, Item source, Skill skill, short num, float counter);

	/**
	 * Skills {@link CounterTypes#COUNTER_TYPE_SKILLIDS} (18)
	 */
	boolean action(Action action, Creature performer, Skill skill, short num, float counter);

	//
	// Floors
	//
	/**
	 * Floor {@link CounterTypes#COUNTER_TYPE_FLOORS} (23)
	 */
	boolean action(Action action, Creature performer, Item source, boolean onSurface, Floor target, int encodedTile, short num, float counter);

	/**
	 * Floor {@link CounterTypes#COUNTER_TYPE_FLOORS} (23)
	 * Unused
	 */
	@Deprecated
	boolean action(Action action, Creature performer, boolean onSurface, Floor floor, int encodedTile, short num, float counter);

	//
	// Tile border
	//
	/**
	 * Tileborder {@link CounterTypes#COUNTER_TYPE_TILEBORDER} (12)
	 */
	boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, int heightOffset, Tiles.TileBorderDirection dir, long borderId, short num, float counter);

	/**
	 * Tileborder {@link CounterTypes#COUNTER_TYPE_TILEBORDER} (12)
	 */
	boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, Tiles.TileBorderDirection dir, long borderId, short num, float counter);

	//
	// Bridges
	//
	/**
	 * Bridges {@link CounterTypes#COUNTER_TYPE_BRIDGE_PARTS} (28)
	 */
	boolean action(Action action, Creature performer, boolean onSurface, BridgePart bridgePart, int encodedTile, short num, float counter);

	/**
	 * Bridges {@link CounterTypes#COUNTER_TYPE_BRIDGE_PARTS} (28)
	 */
	boolean action(Action action, Creature performer, Item item, boolean onSurface, BridgePart bridgePart, int encodedTile, short num, float counter);

	//
	// Cave tiles
	//
	/**
	 * Cave tile {@link CounterTypes#COUNTER_TYPE_CAVETILES} (17)
	 * @since 1.5
	 */
	boolean action(Action action, Creature performer, int tilex, int tiley, boolean onSurface, int tile, int dir, short num, float counter);

	/**
	 * Cave tile {@link CounterTypes#COUNTER_TYPE_CAVETILES} (17)
	 * @since 1.5
	 */
	boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, int heightOffset, int tile, int dir, short num, float counter);
}
