package org.gotti.wurmunlimited.mods.serverfixes;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.ItemTemplatesCreatedListener;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;

import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.items.ItemTypes;

public class ServerFixesMod implements WurmServerMod, Initable, PreInitable, ServerStartedListener, ItemTemplatesCreatedListener, ItemTypes, MiscConstants {

	private static Logger logger = Logger.getLogger(ServerFixesMod.class.getName());

	@Override
	public void onItemTemplatesCreated() {
	}

	@Override
	public void onServerStarted() {
	}

	@Override
	public void init() {
	}

	@Override
	public void preInit() {
		logger.log(Level.INFO, "Registering server fixes");
	}

}
