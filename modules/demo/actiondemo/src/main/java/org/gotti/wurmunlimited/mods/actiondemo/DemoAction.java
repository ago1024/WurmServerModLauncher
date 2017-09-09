package org.gotti.wurmunlimited.mods.actiondemo;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modsupport.actions.ActionEntryBuilder;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModAction;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.players.Player;

/**
 * Demo action.
 * Use a branch on a tile to create a christmas tree.
 */
public class DemoAction implements ModAction, BehaviourProvider, ActionPerformer {

	private static Logger logger = Logger.getLogger(DemoAction.class.getName());

	private final short actionId;
	private final ActionEntry actionEntry;

	/**
	 * Create the action.
	 */
	public DemoAction() {
		// Get the action id
		actionId = (short) ModActions.getNextActionId();
		// Create the action entry
		actionEntry = new ActionEntryBuilder(actionId, "Use", "using", new int[] { 6 /* ACTION_TYPE_NOMOVE */, 48 /* ACTION_TYPE_ENEMY_ALWAYS */, 36 /* ACTION_TYPE_ALWAYS_USE_ACTIVE_ITEM */}).build();
		// Register the action entry
		ModActions.registerAction(actionEntry);
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item object, int tilex, int tiley, boolean onSurface, int tile) {
		if (performer instanceof Player && object != null && object.getTemplateId() == ItemList.branch) {
			return Arrays.asList(actionEntry);
		} else {
			return null;
		}
	}

	@Override
	public short getActionId() {
		return actionId;
	}

	@Override
	public boolean action(Action action, Creature performer, Item source, int tilex, int tiley, boolean onSurface, int heightOffset, int tile, short num, float counter) {
		try {
			if (counter == 1.0f) {
				performer.getCommunicator().sendNormalServerMessage("You start to wave the branch.");

				final int time = 50;
				performer.getCurrentAction().setTimeLeft(time);
				performer.sendActionControl("Waving the " + source.getName(), true, time);
				
			} else {
				int time = 0;

				time = performer.getCurrentAction().getTimeLeft();

				if (counter * 10.0f > time) {
					Item item = ItemFactory.createItem(ItemList.christmasTree, 99.0f, performer.getName());
					item.putItemInfrontof(performer);
					performer.getCommunicator().sendNormalServerMessage("You create a " + item.getName() + " in front of you.");
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return true;
		}
	}
}
