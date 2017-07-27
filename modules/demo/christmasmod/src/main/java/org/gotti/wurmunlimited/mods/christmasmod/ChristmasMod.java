package org.gotti.wurmunlimited.mods.christmasmod;

import java.time.Year;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.callbacks.CallbackApi;
import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.mods.christmasmod.OpenPresentActionPerformer.GiftData;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;
import org.gotti.wurmunlimited.modsupport.properties.ModPlayerProperties;
import org.gotti.wurmunlimited.modsupport.properties.Property;

import com.wurmonline.server.players.Player;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class ChristmasMod implements WurmServerMod, PreInitable, Configurable, ServerStartedListener {

	private static final String PROPERTY_NAME = "christmasmod.present";

	private static final Logger LOGGER = Logger.getLogger(ChristmasMod.class.getName());
	
	int present2015 = 972;
	int present2016 = 972;
	int present2017 = 972;
	int present2018 = 972;
	int present2019 = 972;
	int present2020 = 972;

	@Override
	public void configure(Properties properties) {
		present2015 = Integer.valueOf(properties.getProperty("present2015", String.valueOf(present2015)));
		present2016 = Integer.valueOf(properties.getProperty("present2016", String.valueOf(present2016)));
		present2017 = Integer.valueOf(properties.getProperty("present2017", String.valueOf(present2017)));
		present2018 = Integer.valueOf(properties.getProperty("present2018", String.valueOf(present2018)));
		present2019 = Integer.valueOf(properties.getProperty("present2019", String.valueOf(present2019)));
		present2020 = Integer.valueOf(properties.getProperty("present2020", String.valueOf(present2020)));

		LOGGER.log(Level.INFO, "present2015: " + present2015);
		LOGGER.log(Level.INFO, "present2016: " + present2016);
		LOGGER.log(Level.INFO, "present2017: " + present2017);
		LOGGER.log(Level.INFO, "present2018: " + present2018);
		LOGGER.log(Level.INFO, "present2019: " + present2019);
		LOGGER.log(Level.INFO, "present2020: " + present2020);
	}

	@Override
	public void preInit() {

		ModActions.init();

		try {
			ClassPool classPool = HookManager.getInstance().getClassPool();

			HookManager.getInstance().addCallback(classPool.get("com.wurmonline.server.behaviours.ItemBehaviour"), "christmasmod", this);
			HookManager.getInstance().addCallback(classPool.get("com.wurmonline.server.behaviours.CreatureBehaviour"), "christmasmod", this);

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
					} else if (m.getClassName().equals("com.wurmonline.server.players.PlayerInfo") && m.getMethodName().equals("setReimbursed")) {
						m.replace("christmasmod.setPlayerReceivedPresent((com.wurmonline.server.players.Player)performer);");
					}
				}
			});

			// boolean com.wurmonline.server.behaviours.ItemBehaviour.action(final Action act, final Creature performer, final Item target, final short action, final float counter)
			final String descriptor = Descriptor.ofMethod(CtClass.booleanType, new CtClass[] {
					classPool.get("com.wurmonline.server.behaviours.Action"),
					classPool.get("com.wurmonline.server.creatures.Creature"),
					classPool.get("com.wurmonline.server.items.Item"),
					CtClass.shortType,
					CtClass.floatType
					});
			classPool.get("com.wurmonline.server.behaviours.ItemBehaviour").getMethod("action", descriptor).instrument(new ExprEditor() {
				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					// com.wurmonline.server.items.Item.setAuxData(byte)
					if (m.getClassName().equals("com.wurmonline.server.players.Player") && m.getMethodName().equals("isReimbursed")) {
						m.replace("$_ = christmasmod.hasPlayerReceivedPresent($0);");
					}
				}
			});
			
			// void com.wurmonline.server.behaviours.CreatureBehaviour.handle_ASK_GIFT(Creature, Creature)
			final String descriptor2 = Descriptor.ofMethod(CtClass.voidType, new CtClass[] {
					classPool.get("com.wurmonline.server.creatures.Creature"),
					classPool.get("com.wurmonline.server.creatures.Creature")
					});
			classPool.get("com.wurmonline.server.behaviours.CreatureBehaviour").getMethod("handle_ASK_GIFT", descriptor2).instrument(new ExprEditor() {
				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					// com.wurmonline.server.items.Item.setAuxData(byte)
					if (m.getClassName().equals("com.wurmonline.server.players.Player") && m.getMethodName().equals("isReimbursed")) {
						m.replace("$_ = christmasmod.hasPlayerReceivedPresent($0);");
					}
				}
			});
			

		} catch (NotFoundException | CannotCompileException e) {
			throw new HookException(e);
		}
	}

	@Override
	public void onServerStarted() {
		LOGGER.log(Level.INFO, "registering actions");
		ModActions.registerActionPerformer(new OpenPresentActionPerformer(this::createGiftData));
	}
	
	private GiftData createGiftData(byte auxdata) {
		switch (auxdata) {
		case 8:
			return new GiftData(present2015);
		case 9:
			return new GiftData(present2016);
		case 10:
			return new GiftData(present2017);
		case 11:
			return new GiftData(present2018);
		case 12:
			return new GiftData(present2019);
		case 13:
			return new GiftData(present2020);
		default:
			return OpenPresentActionPerformer.getDefaultPresentData(auxdata);
		}
	}
	
	@CallbackApi
	public boolean hasPlayerReceivedPresent(Player player) {
		List<Property> properties = ModPlayerProperties.getInstance().getPlayerProperties(PROPERTY_NAME, player.getWurmId());
		Long currentYear = Long.valueOf(Year.now().getValue());
		return properties.stream().map(Property::getIntValue).anyMatch(currentYear::equals);
	}
	
	@CallbackApi
	public void setPlayerReceivedPresent(Player player) {
		ModPlayerProperties.getInstance().setPlayerProperty(PROPERTY_NAME, player.getWurmId(), Year.now().getValue());
	}
}
