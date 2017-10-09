package org.gotti.wurmunlimited.mods.httpserver;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.ModEntry;
import org.gotti.wurmunlimited.modloader.interfaces.ModListener;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;

public class HttpServerMod implements WurmServerMod, Initable, Configurable, ServerStartedListener, ModListener {

	private Logger logger = Logger.getLogger(HttpServerMod.class.getName());

	private int serverPort = 0;
	private String publicServerAddress = null;
	private String internalServerAddress = null;
	private int publicServerPort = 0;

	@Override
	public void init() {
	}

	@Override
	public void configure(Properties properties) {
		this.serverPort = Integer.parseInt(properties.getProperty("serverPort", Integer.toString(serverPort)));
		this.publicServerPort = Integer.parseInt(properties.getProperty("publicServerPort", Integer.toString(publicServerPort)));
		this.publicServerAddress = properties.getProperty("publicServerAddress");
		this.internalServerAddress = properties.getProperty("internalServerAddress");

		logger.info("serverPort: " + serverPort);
		logger.info("publicServerAddress: " + publicServerAddress);
		logger.info("publicServerPort: " + publicServerPort);
		logger.info("internalServerAddress: " + internalServerAddress);
		
		ModHttpServerImpl.getInstance().configure(this.serverPort, this.publicServerAddress, this.publicServerPort, this.internalServerAddress);
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
	public void modInitialized(ModEntry<?> entry) {
		ModHttpServerImpl.getInstance().addModEntry(entry);
	}
}
