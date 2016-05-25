package org.gotti.wurmunlimited.modloader.interfaces;

import com.wurmonline.server.creatures.Communicator;

public interface PlayerMessageListener {

	default boolean onPlayerMessage(Communicator communicator, String message, String title) {
		return onPlayerMessage(communicator, message);
	}

	boolean onPlayerMessage(Communicator communicator, String message);

}
