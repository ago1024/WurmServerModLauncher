package org.gotti.wurmunlimited.mods.actiondemo;

import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ModAction;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;

public class ExamineAction implements ModAction {
	
	private final int i;
	
	public ExamineAction(int i) {
		this.i = i;
	}

	@Override
	public ActionPerformer getActionPerformer() {
		return new ActionPerformer() {
			

			@Override
			public short getActionId() {
				return Actions.EXAMINE;
			}
			
			public boolean action(Action action, Creature performer, Item target, short num, float counter) {
				performer.getCommunicator().sendNormalServerMessage("Examine test: " + i);
				return ActionPerformer.super.action(action, performer, target, num, counter);
			}
		};
	}

	
	

}
