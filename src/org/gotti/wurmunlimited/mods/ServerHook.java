package org.gotti.wurmunlimited.mods;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ServerHook {
	
	private List<WurmMod> mods = new CopyOnWriteArrayList<WurmMod>();
	private Handler serverHookHandler = new ServerHookHandler();
	
	public ServerHook() {
		Logger logger = Logger.getLogger("com.wurmonline.server");
		logger.addHandler(serverHookHandler);
	}

	public void addMods(List<WurmMod> wurmMods) {
		mods.addAll(wurmMods);
	}
	
	public void fireOnServerStarted() {
		for (WurmMod mod : mods) {
			try {
				mod.onServerStarted();
			} catch (Exception e) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "onServerStarted handler for mod " + mod.getClass().getSimpleName() + " failed", e);
			}
		}
	}
	
	private class ServerHookHandler extends Handler {
		@Override
		public void publish(LogRecord record) {
			if (record != null && "End of game server initialisation".equals(record.getMessage())) {
				fireOnServerStarted();
			}
		}

		@Override
		public void flush() {
		}

		@Override
		public void close() throws SecurityException {
		}
	}

}
