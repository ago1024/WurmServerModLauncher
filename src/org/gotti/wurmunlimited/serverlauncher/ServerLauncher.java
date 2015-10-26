package org.gotti.wurmunlimited.serverlauncher;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.gotti.wurmunlimited.modloader.LoggingServerHook;
import org.gotti.wurmunlimited.modloader.ModLoader;
import org.gotti.wurmunlimited.mods.WurmMod;

import com.wurmonline.server.gui.WurmServerGuiMain;

public class ServerLauncher {

	public static void main(String[] args) {
		try {
			List<WurmMod> wurmMods = new ModLoader().loadModsFromModDir(Paths.get("mods"));
			
			new LoggingServerHook().addMods(wurmMods);
			
			

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		WurmServerGuiMain.main(args);

	}

}
