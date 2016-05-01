package org.gotti.wurmunlimited.modsupport.actions;

public interface ModAction {
	
	default BehaviourProvider getBehaviourProvider() {
		return null;
	}
	
	default ActionPerformer getActionPerformer() {
		return null;
	}
}
