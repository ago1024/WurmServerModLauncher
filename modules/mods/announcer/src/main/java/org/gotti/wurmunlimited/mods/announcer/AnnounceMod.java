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
	int announceMaxPower = 0;

	@Override
	public void configure(Properties properties) {
		announcePlayers = Boolean.valueOf(properties.getProperty("announcePlayers", String.valueOf(announcePlayers)));
		announceMaxPower = Integer.valueOf(properties.getProperty("announceMaxPower", String.valueOf(announceMaxPower)));

		Logger.getLogger(AnnounceMod.class.getName()).log(Level.INFO, "announcePlayers: " + announcePlayers);
		Logger.getLogger(AnnounceMod.class.getName()).log(Level.INFO, "announceMaxPower: " + announceMaxPower);
	}

	@Override
	public void preInit() {
	}

	@Override
	public void init() {
	}

	@Override
	public void onPlayerLogin(Player player) {
		if (announcePlayers && player.getPower() <= announceMaxPower) {
			MessageServer.broadCastSafe("Player " + player.getName() + " has logged in.", (byte) 1);
		}
	}

}
