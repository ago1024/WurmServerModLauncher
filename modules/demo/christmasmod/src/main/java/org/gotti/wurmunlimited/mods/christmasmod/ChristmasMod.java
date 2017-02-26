package org.gotti.wurmunlimited.mods.christmasmod;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.mods.christmasmod.OpenPresentActionPerformer.GiftData;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class ChristmasMod implements WurmServerMod, PreInitable, Configurable, ServerStartedListener {

	int present2015 = 972;
	int present2016 = 972;

	@Override
	public void configure(Properties properties) {
		present2015 = Integer.valueOf(properties.getProperty("present2015", String.valueOf(present2015)));
		present2016 = Integer.valueOf(properties.getProperty("present2016", String.valueOf(present2016)));

		Logger.getLogger(ChristmasMod.class.getName()).log(Level.INFO, "present2015: " + present2015);
		Logger.getLogger(ChristmasMod.class.getName()).log(Level.INFO, "present2016: " + present2016);
	}

	@Override
	public void preInit() {

		ModActions.init();

		try {
			ClassPool classPool = HookManager.getInstance().getClassPool();

			CtClass ctWurmCalendar = classPool.get("com.wurmonline.server.WurmCalendar");

			// com.wurmonline.server.WurmCalendar.isChristmas()
			ctWurmCalendar.getMethod("isChristmas", "()Z").setBody("return nowIsBetween(17, 0, 23, 11, java.time.Year.now().getValue(), 6, 0, 29, 11, java.time.Year.now().getValue());");

			// com.wurmonline.server.WurmCalendar.isBeforeChristmas()
			ctWurmCalendar.getMethod("isBeforeChristmas", "()Z").setBody("return nowIsBefore(17, 0, 23, 11, java.time.Year.now().getValue());");

			// com.wurmonline.server.WurmCalendar.isAfterChristmas()
			ctWurmCalendar.getMethod("isAfterChristmas", "()Z").setBody("return nowIsAfter(6, 0, 29, 11, java.time.Year.now().getValue());");

			// com.wurmonline.server.behaviours.ItemBehaviour.awardChristmasPresent(Creature)
			classPool.get("com.wurmonline.server.behaviours.ItemBehaviour").getMethod("awardChristmasPresent", Descriptor.ofMethod(CtClass.voidType, new CtClass[] { classPool.get("com.wurmonline.server.creatures.Creature") })).instrument(new ExprEditor() {
				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					// com.wurmonline.server.items.Item.setAuxData(byte)
					if (m.getClassName().equals("com.wurmonline.server.items.Item") && m.getMethodName().equals("setAuxData")) {
						StringBuffer code = new StringBuffer();
						code.append("if (java.time.Year.now().getValue() > 2007) {\n");
						code.append("    $_ = $proceed((byte)(java.time.Year.now().getValue() - 2007));\n");
						code.append("} else {\n");
						code.append("    $_ = $proceed($$);\n");
						code.append("}");
						m.replace(code.toString());
					}
				}
			});

			classPool.get("com.wurmonline.server.players.Player").getMethod("reimburse", "()V").instrument(new ExprEditor() {
				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					// com.wurmonline.server.players.PlayerInfo.setReimbursed(boolean)
					if (m.getClassName().equals("com.wurmonline.server.players.PlayerInfo") && m.getMethodName().equals("setReimbursed")) {
						m.replace("if (com.wurmonline.server.Servers.localServer.testServer || getPower() >= 4) { $proceed($$); };");
					}
				}
			});

		} catch (NotFoundException | CannotCompileException e) {
			throw new HookException(e);
		}
	}

	@Override
	public void onServerStarted() {
		Logger.getLogger(ChristmasMod.class.getName()).log(Level.INFO, "registering actions");
		ModActions.registerActionPerformer(new OpenPresentActionPerformer(this::createGiftData));
	}
	
	private GiftData createGiftData(byte auxdata) {
		switch (auxdata) {
		case 8:
			return new GiftData(present2015);
		case 9:
			return new GiftData(present2016);
		default:
			return OpenPresentActionPerformer.getDefaultPresentData(auxdata);
		}
	}

}
