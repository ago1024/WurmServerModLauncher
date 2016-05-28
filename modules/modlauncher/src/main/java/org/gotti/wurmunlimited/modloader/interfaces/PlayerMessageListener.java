package org.gotti.wurmunlimited.modloader.interfaces;

import com.wurmonline.server.creatures.Communicator;

public interface PlayerMessageListener {

	/**
	 * Handle a player message
	 * @param communicator Communicator
	 * @param message message
	 * @param title chat title
	 * @return MessagePolicy describing how to further process the message
	 */
	default MessagePolicy onPlayerMessage(Communicator communicator, String message, String title) {
		return onPlayerMessage(communicator, message) ? MessagePolicy.DISCARD : MessagePolicy.PASS;
	}

	/**
	 * Handle a player message. Use {@link #onPlayerMessage(Communicator, String, String)} instead
	 * @param communicator Communicator
	 * @param message message
	 * @return true if the message should not be processed by the game
	 */
	@Deprecated
	boolean onPlayerMessage(Communicator communicator, String message);

}
