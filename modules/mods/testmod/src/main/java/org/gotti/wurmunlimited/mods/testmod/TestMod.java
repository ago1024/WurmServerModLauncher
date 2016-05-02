package org.gotti.wurmunlimited.mods.testmod;

import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.ItemTemplatesCreatedListener;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemTypes;

public class TestMod implements WurmServerMod, Initable, PreInitable, ServerStartedListener, ItemTemplatesCreatedListener, ItemTypes, MiscConstants {

	private static Logger logger = Logger.getLogger(TestMod.class.getName());

	@Override
	public void onItemTemplatesCreated() {
	}

	@Override
	public void onServerStarted() {
	}

	@Override
	public void init() {
	}

	public static boolean willMineSlope(Creature performer, Item source) {
		return true;
	}

	@Override
	public void preInit() {
		ModActions.init();
	}

}
