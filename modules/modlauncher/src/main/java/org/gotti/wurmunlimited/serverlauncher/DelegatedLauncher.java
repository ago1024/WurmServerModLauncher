package org.gotti.wurmunlimited.serverlauncher;

import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.ModLoader;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modloader.server.ServerHook;

public class DelegatedLauncher {
	
	public static void main(String[] args) {
		
		try {
			List<WurmServerMod> wurmMods = new ModLoader().loadModsFromModDir(Paths.get("mods"));
			ServerHook.createServerHook().addMods(wurmMods);
			
			
			String[] classes = {
					"com.wurmonline.server.gui.WurmServerGuiMainDeferred",
					"com.wurmonline.server.gui.WurmServerGuiMain"
			};
			
			for (String classname : classes) {
				
				try {
					HookManager.getInstance().getLoader().run(classname, args);
					return;
				} catch (ClassNotFoundException e) {
					continue;
				}
			}
			
			throw new ClassNotFoundException("com.wurmonline.server.gui.WurmServerGuiMain");
		} catch (Throwable e) {
			Logger.getLogger(DelegatedLauncher.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
