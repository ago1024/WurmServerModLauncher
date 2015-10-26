package org.gotti.wurmunlimited.modloader;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.mods.ServerStartedListener;
import org.gotti.wurmunlimited.mods.WurmMod;

public class ServerHook {
	
	private List<WurmMod> mods = new CopyOnWriteArrayList<WurmMod>();
	
	protected ServerHook() {
	}

	public void addMods(List<WurmMod> wurmMods) {
		mods.addAll(wurmMods);
	}
	
	public void fireOnServerStarted() {
		for (WurmMod mod : mods) {
			try {
				if (mod instanceof ServerStartedListener) {
					((ServerStartedListener) mod).onServerStarted();
				}
			} catch (Exception e) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "onServerStarted handler for mod " + mod.getClass().getSimpleName() + " failed", e);
			}
		}
	}
}
