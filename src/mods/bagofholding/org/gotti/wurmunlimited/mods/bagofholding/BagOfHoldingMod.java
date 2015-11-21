package org.gotti.wurmunlimited.mods.bagofholding;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.classhooks.InvocationHandlerFactory;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import com.wurmonline.server.deities.Deities;
import com.wurmonline.server.deities.Deity;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.spells.BagOfHolding;
import com.wurmonline.server.spells.Spells;

public class BagOfHoldingMod implements WurmMod, Initable, PreInitable, Configurable, ServerStartedListener {
	
	private int spellCost = 30;
	private int spellDifficulty = 20;
	private long spellCooldown = 300000L;
	private int effectModifier = 0;
	
	private static final Logger logger = Logger.getLogger(BagOfHoldingMod.class.getName());
	
	@Override
	public void onServerStarted() {
		new Runnable() {
			
			@Override
			public void run() {
				logger.log(Level.INFO, "Registering BagOfHolding spell");

				BagOfHolding bagOfHolding = new BagOfHolding(spellCost, spellDifficulty, spellCooldown);
				
				try {
					ReflectionUtil.callPrivateMethod(Spells.class, ReflectionUtil.getMethod(Spells.class, "addSpell"), bagOfHolding);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e ) {
					throw new RuntimeException(e);
				}
				
				for (Deity deity : Deities.getDeities()) {
					deity.addSpell(bagOfHolding);
				}
			}
		}.run();
	}
	
	@Override
	public void configure(Properties properties) {
		spellCost = Integer.valueOf(properties.getProperty("spellCost", Integer.toString(spellCost)));
		spellDifficulty = Integer.valueOf(properties.getProperty("spellDifficulty", Integer.toString(spellDifficulty)));
		spellCooldown = Long.valueOf(properties.getProperty("spellCooldown", Long.toString(spellCooldown)));
		effectModifier = Integer.valueOf(properties.getProperty("effectModifier", Integer.toString(effectModifier)));
		
		logger.log(Level.INFO, "spellCost: " + spellCost);
		logger.log(Level.INFO, "spellDifficulty: " + spellDifficulty);
		logger.log(Level.INFO, "spellCooldown: " + spellCooldown);
		logger.log(Level.INFO, "effectModifier: " + effectModifier);
	}
	
	@Override
	public void preInit() {
		ModActions.init();
	}

	@Override
	public void init() {
		HookManager.getInstance().registerHook("com.wurmonline.server.items.Item", "getContainerVolume", "()I", new InvocationHandlerFactory() {
			
			@Override
			public InvocationHandler createInvocationHandler() {
				return new InvocationHandler() {
					
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						Object volume = method.invoke(proxy, args);
						
						if (volume instanceof Number && proxy instanceof Item && BagOfHolding.isValidTarget((Item) proxy)) {
							Item target = (Item)proxy;
							
							float modifier = BagOfHolding.getSpellEffect(target);
							
							if (effectModifier == 0) {
								if (modifier > 1) {
									double newVolume = Math.min(Integer.MAX_VALUE, modifier * ((Number) volume).doubleValue());
									return (int) newVolume;
								}
							} else if (modifier > 0) {
								double scale = 1 + modifier * modifier * effectModifier * 0.0001;
								double newVolume = Math.min(Integer.MAX_VALUE, scale * ((Number) volume).doubleValue());
								return (int) newVolume;
							}
						}
						
						return volume;
					}
				};
			}
		});
	}
}
