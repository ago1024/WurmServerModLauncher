package org.gotti.wurmunlimited.mods.announcer;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.PlayerLoginListener;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;

import com.wurmonline.server.MessageServer;
import com.wurmonline.server.players.Player;

public class AnnounceMod implements WurmServerMod, Initable, PreInitable, Configurable, PlayerLoginListener {

	boolean announcePlayers = true;

	@Override
	public void configure(Properties properties) {
		announcePlayers = Boolean.valueOf(properties.getProperty("announcePlayrs", String.valueOf(announcePlayers)));

		Logger.getLogger(AnnounceMod.class.getName()).log(Level.INFO, "announcePlayers: " + announcePlayers);
	}

	@Override
	public void preInit() {
	}

	@Override
	public void init() {
	}

	@Override
	public void onPlayerLogin(Player player) {
		if (announcePlayers) {
			MessageServer.broadCastSafe("Player " + player.getName() + " has logged in.", (byte) 1);
		}
	}

}
