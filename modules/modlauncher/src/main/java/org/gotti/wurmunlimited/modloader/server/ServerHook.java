package org.gotti.wurmunlimited.modloader.server;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modcomm.ModComm;
import org.gotti.wurmunlimited.modloader.interfaces.ItemTemplatesCreatedListener;
import org.gotti.wurmunlimited.modloader.interfaces.PlayerLoginListener;
import org.gotti.wurmunlimited.modloader.interfaces.PlayerMessageListener;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;

import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.players.Player;

public class ServerHook {
	
	private List<WurmServerMod> mods = new CopyOnWriteArrayList<WurmServerMod>();
	
	protected ServerHook() {
	}

	public void addMods(List<WurmServerMod> wurmMods) {
		mods.addAll(wurmMods);
	}
	
	public void fireOnServerStarted() {
		ModComm.serverStarted();
		for (WurmServerMod mod : mods) {
			try {
				if (mod instanceof ServerStartedListener) {
					((ServerStartedListener) mod).onServerStarted();
				}
			} catch (Exception e) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "onServerStarted handler for mod " + mod.getClass().getSimpleName() + " failed", e);
			}
		}
	}
	
	public void fireOnItemTemplatesCreated() {
		for (WurmServerMod mod : mods) {
			try {
				if (mod instanceof ItemTemplatesCreatedListener) {
					((ItemTemplatesCreatedListener) mod).onItemTemplatesCreated();
				}
			} catch (Exception e) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "onItemTemplatesCreated handler for mod " + mod.getClass().getSimpleName() + " failed", e);
			}
		}
	}

	public boolean fireOnMessage(Communicator communicator, String message) {
		boolean state = false;
		for (WurmServerMod mod : mods) {
			try {
				if (mod instanceof PlayerMessageListener) {
					state |= ((PlayerMessageListener) mod).onPlayerMessage(communicator, message);
				}
			} catch (Exception e) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "onPlayerMessage handler for mod " + mod.getClass().getSimpleName() + " failed", e);
			}
		}
		return state;
	}
	
	public void fireOnPlayerLogin(Player player) {
		ModComm.playerConnected(player);
		for (WurmServerMod mod : mods) {
			try {
				if (mod instanceof PlayerLoginListener) {
					((PlayerLoginListener) mod).onPlayerLogin(player);
				}
			} catch (Exception e) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "onPlayerLogin handler for mod " + mod.getClass().getSimpleName() + " failed", e);
			}
		}
	}
	
	public static ServerHook createServerHook() {
		return ProxyServerHook.getInstance();
	}
}
