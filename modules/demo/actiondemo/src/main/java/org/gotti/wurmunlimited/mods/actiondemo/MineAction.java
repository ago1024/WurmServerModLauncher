package org.gotti.wurmunlimited.mods.actiondemo;

import static org.gotti.wurmunlimited.modsupport.actions.ActionPropagation.FINISH_ACTION;
import static org.gotti.wurmunlimited.modsupport.actions.ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION;
import static org.gotti.wurmunlimited.modsupport.actions.ActionPropagation.NO_SERVER_PROPAGATION;

import java.util.Arrays;
import java.util.List;

import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModAction;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.behaviours.NoSuchActionException;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;

public class MineAction implements ModAction, BehaviourProvider, ActionPerformer {
	
	@Override
	public short getActionId() {
		return Actions.MINE;
	}

	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item subject, int tileX, int tileY, boolean onSurface, Tiles.TileBorderDirection borderDirection, boolean border, int heightOffset) {
		if (subject.getTemplateId() == ItemList.pickAxe || !border) {
			return Arrays.asList(Actions.actionEntrys[Actions.MINE]);
		}
		return null;
	}

	@Override
	public boolean action(Action action, Creature performer, Item source, int tileX, int tileY, boolean onSurface, int heightOffset, Tiles.TileBorderDirection borderDirection, long borderId, short actionId, float counter) {
		
		try {
			if (counter == 1.0f) {
				performer.getCommunicator().sendNormalServerMessage("You start to hit the border with the " + source.getName() + ".");
	
				final int time = 50;
				performer.getCurrentAction().setTimeLeft(time);
				performer.sendActionControl("Swinging the " + source.getName(), true, time);
				
			} else {
				int time = performer.getCurrentAction().getTimeLeft();
				if (counter * 10.0f > time) {
					performer.getCommunicator().sendNormalServerMessage("Nothing happens.");
					
					// Finish the action. Do not send it to the server or other mods
					return propagate(action, FINISH_ACTION, NO_SERVER_PROPAGATION, NO_ACTION_PERFORMER_PROPAGATION);
				}
			}
			// Continue the action. Do not send it to the server or other mods
			return propagate(action, FINISH_ACTION, NO_SERVER_PROPAGATION, NO_ACTION_PERFORMER_PROPAGATION);
		} catch (NoSuchActionException e) {
			return true;
		}
	}
	
	@Override
	public boolean defaultPropagation(Action action) {
		return ActionPerformer.super.defaultPropagation(action);
	}
}
