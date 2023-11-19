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

	private boolean announcePlayers = true;
	private boolean announcePlayerLogout = true;
	private int announceMaxPower = 0;

	@Override
	public void configure(Properties properties) {
		announcePlayers = Boolean.parseBoolean(properties.getProperty("announcePlayers", String.valueOf(announcePlayers)));
		announcePlayerLogout = Boolean.parseBoolean(properties.getProperty("announcePlayerLogout", String.valueOf(announcePlayerLogout)));
		announceMaxPower = Integer.parseInt(properties.getProperty("announceMaxPower", String.valueOf(announceMaxPower)));

		final Logger logger = Logger.getLogger(AnnounceMod.class.getName());
		logger.log(Level.INFO, "announcePlayers: " + announcePlayers);
		logger.log(Level.INFO, "announcePlayerLogout: " + announcePlayerLogout);
		logger.log(Level.INFO, "announceMaxPower: " + announceMaxPower);
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
	
	public void onPlayerLogout(Player player) {
		if (this.announcePlayerLogout && player.getPower() <= this.announceMaxPower) {
			MessageServer.broadCastSafe("Player " + player.getName() + " has logged out.", (byte) 1);
		}
	}

}
