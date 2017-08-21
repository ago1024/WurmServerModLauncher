package org.gotti.wurmunlimited.mods.actiondemo;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.ItemTemplatesCreatedListener;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.items.ItemTypes;

public class ActionDemoMod implements WurmServerMod, Initable, PreInitable, ServerStartedListener, ItemTemplatesCreatedListener, ItemTypes, MiscConstants {

	private static Logger logger = Logger.getLogger(ActionDemoMod.class.getName());

	@Override
	public void onItemTemplatesCreated() {
	}

	@Override
	public void onServerStarted() {
		logger.log(Level.INFO, "Registering demo actions");
		ModActions.registerAction(new DemoAction());
		ModActions.registerAction(new TestAction());
		ModActions.registerAction(new ExamineAction(1));
		ModActions.registerAction(new ExamineAction(2));
		ModActions.registerAction(new MineAction());
	}

	@Override
	public void init() {
	}

	@Override
	public void preInit() {
		ModActions.init();
	}

}
