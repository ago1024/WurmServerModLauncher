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

	private final ChristmasModConfiguration config = new ChristmasModConfiguration();


	@Override
	public void configure(Properties properties) {
		config.configure(properties);
	}

	@Override
	public void preInit() {

		ModActions.init();

		try {
			ClassPool classPool = HookManager.getInstance().getClassPool();

			HookManager.getInstance().addCallback(classPool.get("com.wurmonline.server.behaviours.ItemBehaviour"), "christmasmod", this);
			HookManager.getInstance().addCallback(classPool.get("com.wurmonline.server.behaviours.CreatureBehaviour"), "christmasmod", this);

			HookManager.getInstance().addCallback(classPool.get("com.wurmonline.server.WurmCalendar"), "christmasmodcalendar", config.createCalendar());

			CtClass ctWurmCalendar = classPool.get("com.wurmonline.server.WurmCalendar");

			// com.wurmonline.server.WurmCalendar.isChristmas()
			ctWurmCalendar.getMethod("isChristmas", "()Z").setBody("return christmasmodcalendar.isChristmas();");

			// com.wurmonline.server.WurmCalendar.isBeforeChristmas()
			ctWurmCalendar.getMethod("isBeforeChristmas", "()Z").setBody("return christmasmodcalendar.isBeforeChristmas();");

			// com.wurmonline.server.WurmCalendar.isAfterChristmas()
			ctWurmCalendar.getMethod("isAfterChristmas", "()Z").setBody("return christmasmodcalendar.isAfterChristmas();");

			// com.wurmonline.server.behaviours.ItemBehaviour.awardChristmasPresent(Creature)
			classPool.get("com.wurmonline.server.behaviours.ItemBehaviour").getMethod("awardChristmasPresent", Descriptor.ofMethod(CtClass.voidType, new CtClass[] { classPool.get("com.wurmonline.server.creatures.Creature") })).instrument(new ExprEditor() {
				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					// com.wurmonline.server.items.Item.setAuxData(byte)
					if (m.getClassName().equals("com.wurmonline.server.items.Item") && m.getMethodName().equals("setAuxData")) {
						String code =
								"if (java.time.Year.now().getValue() > 2007) {\n" +
								"    $_ = $proceed((byte)(java.time.Year.now().getValue() - 2007));\n" +
								"} else {\n" +
								"    $_ = $proceed($$);\n" +
								"}";
						m.replace(code);
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
		if (auxdata >= 8) {
			Integer itemId = config.getPresentItemId(2007 + auxdata);
			if (itemId != null) {
				return new GiftData(itemId);
			}
		}

		return OpenPresentActionPerformer.getDefaultPresentData(auxdata);
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
