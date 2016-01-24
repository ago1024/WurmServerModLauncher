package org.gotti.wurmunlimited.serverlauncher;

import javassist.Loader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

public class ServerLauncher {

	public static void main(String[] args) {
		try {
			
			initLogger();

			Loader loader = HookManager.getInstance().getLoader();
			loader.delegateLoadingOf("javafx.");
			loader.delegateLoadingOf("com.sun.");
			loader.delegateLoadingOf("org.controlsfx.");
			loader.delegateLoadingOf("impl.org.controlsfx");
			loader.delegateLoadingOf("com.mysql.");
			loader.delegateLoadingOf("org.sqlite.");
			loader.delegateLoadingOf("org.gotti.wurmunlimited.modloader.classhooks.");
			loader.delegateLoadingOf("javassist.");

			Thread.currentThread().setContextClassLoader(loader);

			loader.run("org.gotti.wurmunlimited.serverlauncher.DelegatedLauncher", args);
		} catch (Throwable e) {
			Logger.getLogger(ServerLauncher.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			e.printStackTrace();
			System.exit(-1);
		}

	}
	
	private static void initLogger() throws SecurityException, IOException {
		//  Use externally configured loggers
		if (System.getProperty("java.util.logging.config.file") != null) {
			return;
		}
		if (System.getProperty("java.util.logging.config.class") != null) {
			return;
		}
		
		// Use a provided logging.properties file
		Path loggingPropertiesFile = Paths.get("logging.properties");
		if (Files.isRegularFile(loggingPropertiesFile)) {
			System.setProperty("java.util.logging.config.file", loggingPropertiesFile.toString());
			return;
		}
	}

}
