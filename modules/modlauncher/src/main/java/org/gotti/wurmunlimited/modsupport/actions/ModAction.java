package org.gotti.wurmunlimited.modsupport.actions;

public interface ModAction {
	
	default BehaviourProvider getBehaviourProvider() {
		if (this instanceof BehaviourProvider) {
			return (BehaviourProvider) this;
		} else {
			return null;
		}
	}
	
	default ActionPerformer getActionPerformer() {
		if (this instanceof ActionPerformer) {
			return (ActionPerformer) this;
		} else {
			return null;
		}
	}
}
