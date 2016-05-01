package org.gotti.wurmunlimited.modloader.interfaces;

import com.wurmonline.server.creatures.Communicator;

public interface PlayerMessageListener {

	boolean onPlayerMessage(Communicator communicator, String message);

}
