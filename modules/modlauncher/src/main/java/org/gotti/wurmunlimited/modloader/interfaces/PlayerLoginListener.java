package org.gotti.wurmunlimited.modloader.interfaces;

import com.wurmonline.server.players.Player;

public interface PlayerLoginListener {

	void onPlayerLogin(Player player);

	default void onPlayerLogout(Player player) {
	}

}
