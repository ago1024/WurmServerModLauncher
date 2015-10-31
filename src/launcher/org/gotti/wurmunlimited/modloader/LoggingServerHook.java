package org.gotti.wurmunlimited.modloader;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LoggingServerHook extends ServerHook {

	private Handler serverHookHandler = new ServerHookHandler();
	
	public LoggingServerHook() {
		Logger logger = Logger.getLogger("com.wurmonline.server");
		logger.addHandler(serverHookHandler);
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
