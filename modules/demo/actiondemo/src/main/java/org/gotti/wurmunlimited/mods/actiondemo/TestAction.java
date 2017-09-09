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
import com.wurmonline.server.players.Player;

public class TestAction implements ModAction, BehaviourProvider, ActionPerformer {

	private static Logger logger = Logger.getLogger(TestAction.class.getName());

	private final short actionId;
	private final ActionEntry actionEntry;

	public TestAction() {
		actionId = (short) ModActions.getNextActionId();
		actionEntry = new ActionEntryBuilder(actionId, "Test entry", "testing", new int[] { 6 /* ACTION_TYPE_NOMOVE */, 48 /* ACTION_TYPE_ENEMY_ALWAYS */, 37 /* ACTION_TYPE_NEVER_USE_ACTIVE_ITEM */}).build();
		ModActions.registerAction(actionEntry);
	}

	@Override
	public BehaviourProvider getBehaviourProvider() {
		return this;
	}

	@Override
	public ActionPerformer getActionPerformer() {
		return this;
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, Creature target) {
		if (performer instanceof Player) {
			return Arrays.asList(actionEntry);
		} else {
			return null;
		}
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Creature target) {
		return getBehavioursFor(performer, null, target);
	}

	@Override
	public short getActionId() {
		return actionId;
	}

	@Override
	public boolean action(Action action, Creature performer, Creature target, short num, float counter) {
		return action(action, performer, null, target, num, counter);
	}

	@Override
	public boolean action(Action action, Creature performer, Item source, Creature target, short num, float counter) {
		try {
			if (counter == 1.0f) {
				performer.getCommunicator().sendNormalServerMessage("You start to count. " + (int) counter);

				final int time = 50;
				performer.getCurrentAction().setTimeLeft(time);
				performer.sendActionControl("Counting", true, time);
			} else {
				int time = 0;

				time = performer.getCurrentAction().getTimeLeft();

				if (counter * 10.0f <= time) {
					if (action.justTickedSecond()) {
						performer.getCommunicator().sendNormalServerMessage("" + (int) counter);
					}
				} else {
					performer.getCommunicator().sendNormalServerMessage("" + (int) counter + ".You look pleased as the test works.");
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
