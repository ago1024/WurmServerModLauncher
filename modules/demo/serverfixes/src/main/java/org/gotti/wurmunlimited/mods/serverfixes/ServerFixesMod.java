package org.gotti.wurmunlimited.mods.serverfixes;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.ItemTemplatesCreatedListener;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.items.ItemTypes;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class ServerFixesMod implements WurmServerMod, Initable, PreInitable, ServerStartedListener, ItemTemplatesCreatedListener, ItemTypes, MiscConstants {

	private static Logger logger = Logger.getLogger(ServerFixesMod.class.getName());

	@Override
	public void onItemTemplatesCreated() {
	}

	@Override
	public void onServerStarted() {
		logger.log(Level.INFO, "Registering Integration Test");
	}

	@Override
	public void init() {
	}

	@Override
	public void preInit() {
		ModActions.init();
		
		try {
			ClassPool classPool = HookManager.getInstance().getClassPool();
			CtClass ctWurmServerGuiController = classPool.get("com.wurmonline.server.gui.WurmServerGuiController");
			CtMethod ctStartDB = ctWurmServerGuiController.getMethod("startDB", "(Ljava/lang/String;)V");
			ctStartDB.instrument(new ExprEditor() {
				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					if ("setCurrent".equals(m.getMethodName()) && "com.wurmonline.server.gui.folders.GameFolder".equals(m.getClassName())) {
						m.replace("{boolean r = $proceed($$); if (r) { com.wurmonline.server.gui.folders.Folders.setCurrent($0); }; $_ = r; }");
					}
				}
			});
			
			
		} catch (NotFoundException | CannotCompileException e) {
			throw new HookException(e);
		}
	}

}
