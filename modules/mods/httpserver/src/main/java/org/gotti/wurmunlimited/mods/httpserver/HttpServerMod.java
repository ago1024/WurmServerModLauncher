package org.gotti.wurmunlimited.mods.httpserver;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.ModEntry;
import org.gotti.wurmunlimited.modloader.interfaces.ModListener;
import org.gotti.wurmunlimited.modloader.interfaces.ServerShutdownListener;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;

public class HttpServerMod implements WurmServerMod, Initable, Configurable, ServerStartedListener, ServerShutdownListener, ModListener {

	private Logger logger = Logger.getLogger(HttpServerMod.class.getName());

	private int serverPort = 0;
	private String publicServerAddress = null;
	private String internalServerAddress = null;
	private int publicServerPort = 0;
	private int maxThreads = 50;

	@Override
	public void init() {
	}

	@Override
	public void configure(Properties properties) {
		this.serverPort = Integer.parseInt(properties.getProperty("serverPort", Integer.toString(serverPort)));
		this.publicServerPort = Integer.parseInt(properties.getProperty("publicServerPort", Integer.toString(publicServerPort)));
		this.publicServerAddress = properties.getProperty("publicServerAddress");
		this.internalServerAddress = properties.getProperty("internalServerAddress");
		this.maxThreads = Integer.parseInt(properties.getProperty("maxThreads", Integer.toString(maxThreads)));

		logger.info("serverPort: " + serverPort);
		logger.info("publicServerAddress: " + publicServerAddress);
		logger.info("publicServerPort: " + publicServerPort);
		logger.info("internalServerAddress: " + internalServerAddress);
		logger.info("maxThreads: " + maxThreads);
		
		ModHttpServerImpl.getInstance().configure(this.internalServerAddress, this.serverPort, this.publicServerAddress, this.publicServerPort);
		ModHttpServerImpl.getInstance().setMaxThreads(maxThreads);
	}

	@Override
	public void onServerStarted() {
		try {
			ModHttpServerImpl.getInstance().start();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	@Override
	public void onServerShutdown() {
		try {
			ModHttpServerImpl.getInstance().stop();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	@Override
	public void modInitialized(ModEntry<?> entry) {
		ModHttpServerImpl.getInstance().addModEntry(entry);
	}
}
