package org.gotti.wurmunlimited.modsupport.actions;

/**
 * Define how an action is propagated to server and other action performers.
 */
public enum ActionPropagation {
	
	/**
	 * Propagate the action to the server.
	 */
	SERVER_PROPAGATION,
	
	/**
	 * Do not propagate the action to the server.
	 */
	NO_SERVER_PROPAGATION,
	
	/**
	 * Propagate the action to other action performers.
	 */
	ACTION_PERFORMER_PROPAGATION,
	
	/**
	 * Do not propagate the action to other action performers.
	 * Other action performers may have handled the action before.
	 */
	NO_ACTION_PERFORMER_PROPAGATION,
	
	/**
	 * Finish the action. This is equivalent to returning true from the action() method.
	 */
	FINISH_ACTION,
	
	/**
	 * Continue the action. This is equivalent to returning false from the action() method.
	 */
	CONTINUE_ACTION,
}
