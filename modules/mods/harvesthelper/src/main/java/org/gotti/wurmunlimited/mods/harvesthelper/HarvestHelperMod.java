package org.gotti.wurmunlimited.mods.harvesthelper;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.PlayerLoginListener;
import org.gotti.wurmunlimited.modloader.interfaces.PlayerMessageListener;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;

import com.wurmonline.server.Server;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.WurmHarvestables.Harvestable;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.players.Player;

public class HarvestHelperMod implements WurmServerMod, Configurable, Initable, PreInitable, PlayerLoginListener, PlayerMessageListener {

	private static Logger logger = Logger.getLogger(HarvestHelperMod.class.getName());

	private boolean allowMountedHarvest = true;
	private boolean enableSeasonsCommand = true;
	private boolean enableSeasonsMotd = true;

	@Override
	public void configure(Properties properties) {
		this.allowMountedHarvest = Boolean.parseBoolean(properties.getProperty("allowMountedHarvest", Boolean.toString(this.allowMountedHarvest)));
		this.enableSeasonsCommand = Boolean.parseBoolean(properties.getProperty("enableSeasonsCommand", Boolean.toString(this.enableSeasonsCommand)));
		this.enableSeasonsMotd = Boolean.parseBoolean(properties.getProperty("enableSeasonsMotd", Boolean.toString(this.enableSeasonsMotd)));

		logger.log(Level.INFO, "allowMountedHarvest: " + allowMountedHarvest);
		logger.log(Level.INFO, "enableSeasonsCommand: " + enableSeasonsCommand);
		logger.log(Level.INFO, "enableSeasonsMotd: " + enableSeasonsMotd);
	}

	@Override
	public void preInit() {
		if (allowMountedHarvest) {
			initAllowMountedHarvest();
		}
	}

	private void initAllowMountedHarvest() {
		try {
			ClassPool classPool = HookManager.getInstance().getClassPool();

			// com.wurmonline.server.behaviours.Action.Action(Creature, long, long, short, float, float, float, float)
			CtClass ctAction = classPool.get("com.wurmonline.server.behaviours.Action");
			String descriptor = Descriptor.ofConstructor(new CtClass[] { classPool.get("com.wurmonline.server.creatures.Creature"), CtClass.longType, CtClass.longType, CtClass.shortType, CtClass.floatType, CtClass.floatType, CtClass.floatType, CtClass.floatType });

			CtConstructor constructor = ctAction.getConstructor(descriptor);
			constructor.instrument(new ExprEditor() {
				@Override
				public void edit(MethodCall methodCall) throws CannotCompileException {
					if (methodCall.getClassName().equals("com.wurmonline.server.behaviours.Action") && methodCall.getMethodName().equals("isFatigue")) {
						methodCall.replace("$_ = this.action == com.wurmonline.server.behaviours.Actions.HARVEST && this.performer.getVehicle() != -10L ? false : $proceed($$);");
					}
				}
			});
		} catch (NotFoundException | CannotCompileException e) {
			throw new HookException(e);
		}
	}

	@Override
	public void init() {
	}
	
	private List<Harvestable> getSortedHarvestables() {
		final Comparator<Harvestable> comparator = Comparator.comparing(Harvestable::getSeasonStart);
		return Arrays.stream(Harvestable.values()).filter(harvestable -> harvestable != Harvestable.NONE).sorted(comparator.reversed()).collect(Collectors.toList());
	}

	@Override
	public void onPlayerLogin(Player player) {
		if (enableSeasonsMotd && player != null) {
			long now = WurmCalendar.currentTime;
			for (Harvestable harvestable : getSortedHarvestables()) {
				long start = harvestable.getSeasonStart();
				if (now >= start) {
					player.getCommunicator().sendNormalServerMessage(String.format("%s is in season", capitalize(harvestable.getName())));
				} else if (now >= start - 345600L) {
					player.getCommunicator().sendNormalServerMessage(String.format("%s will soon be in season", capitalize(harvestable.getName())));
				}
			}
		}
	}

	@Override
	public boolean onPlayerMessage(Communicator communicator, String message) {
		if (enableSeasonsCommand && message != null && message.startsWith("/seasons")) {
			long now = WurmCalendar.currentTime;
			for (Harvestable harvestable : getSortedHarvestables()) {
				long start = harvestable.getSeasonStart();
				if (now >= start) {
					communicator.sendNormalServerMessage(String.format("%s is in season", capitalize(harvestable.getName())));
				} else {
					long duration = start - now;
					communicator.sendNormalServerMessage(String.format("%s will be in season in %s", capitalize(harvestable.getName()), Server.getTimeFor(duration * 1000 / 8)));
				}
			}
			return true;
		}
		return false;
	}

	private String capitalize(String s) {
		if (s == null || s.length() == 0) {
			return s;
		}
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}
}
