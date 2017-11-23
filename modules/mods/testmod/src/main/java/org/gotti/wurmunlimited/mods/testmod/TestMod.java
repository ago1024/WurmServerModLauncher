package org.gotti.wurmunlimited.mods.testmod;

import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.callbacks.CallbackApi;
import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.ItemTemplatesCreatedListener;
import org.gotti.wurmunlimited.modloader.interfaces.MessagePolicy;
import org.gotti.wurmunlimited.modloader.interfaces.PlayerMessageListener;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerShutdownListener;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;
import org.gotti.wurmunlimited.modsupport.items.ModItems;

import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.items.ItemTypes;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.Descriptor;

public class TestMod implements WurmServerMod, Initable, PreInitable, ServerStartedListener, ItemTemplatesCreatedListener, ItemTypes, MiscConstants, PlayerMessageListener, ServerShutdownListener {

	static Logger logger = Logger.getLogger(TestMod.class.getName());

	@Override
	public void onItemTemplatesCreated() {
	}

	@Override
	public void onServerStarted() {
		ModItems.addModelNameProvider(ItemList.chair, item -> item.getTemplate().getModelName() + "birchwood.foo");
	}
	
	@Override
	public void onServerShutdown() {
		logger.info("Server shutdown");
	}

	@Override
	public void init() {
	}

	@CallbackApi
	public void pollAction(Action action) {
		logger.info("Poll action " + ((Action)action).getActionString());
	}
	
	@Override
	public void preInit() {
		ModActions.init();
		
		try {
			ClassPool classPool = HookManager.getInstance().getClassPool();
			CtClass ctAction = classPool.get("com.wurmonline.server.behaviours.Action");
			
			HookManager.getInstance().addCallback(ctAction, "testModApi", this);
			
			CtMethod ctPoll = ctAction.getMethod("poll", Descriptor.ofMethod(CtClass.booleanType, new CtClass[0]));
			ctPoll.insertBefore("testModApi.pollAction(this);");
		
		} catch (Exception e) {
			throw new HookException(e);
		}
	}
	
	@Override
	public MessagePolicy onPlayerMessage(Communicator communicator, String message, String title) {
		if ("/question".equals(message)) {
			TestQuestion.create(communicator.getPlayer());
			return MessagePolicy.DISCARD;
		}
		return MessagePolicy.PASS;
	}
	
	@Override
	public boolean onPlayerMessage(Communicator communicator, String message) {
		return false;
	}

}
