package org.gotti.wurmunlimited.mods.inbreedwarning;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;

import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.classhooks.InvocationHandlerFactory;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;

import com.wurmonline.server.creatures.Creature;

public class InbreedWarningMod implements WurmMod, Initable {
	
	@Override
	public void init() {

		try {
			ClassPool classPool = HookManager.getInstance().getClassPool();
			
			// com.wurmonline.server.behaviours.MethodsCreatures.breed(Creature, Creature, short, Action, float)
			String descriptor = Descriptor.ofMethod(CtClass.booleanType, new CtClass[] {
					classPool.get("com.wurmonline.server.creatures.Creature"), 
					classPool.get("com.wurmonline.server.creatures.Creature"), 
					CtClass.shortType, 
					classPool.get("com.wurmonline.server.behaviours.Action"), 
					CtClass.floatType
			});
			HookManager.getInstance().registerHook("com.wurmonline.server.behaviours.MethodsCreatures", "breed", descriptor, new InvocationHandlerFactory() {
				
				@Override
				public InvocationHandler createInvocationHandler() {
					return new InvocationHandler() {
						
						@Override
						public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
							final Creature performer = (Creature) args[0];
							final Creature target = (Creature) args[1];
							final Float counter = (Float)args[4];
							
							if (counter != null && counter == 1.0f && performer.getFollowers().length == 1 && performer.getFollowers()[0] != null) {
								Creature breeder = performer.getFollowers()[0];
								
								final Creature mother;
								final Creature father;
								if (breeder.getSex() == 1) {
									mother = breeder;
									father = target;
								} else if (target.getSex() == 1) {
									mother = target;
									father = breeder;
								} else {
									mother = null;
									father = null;
								}
								
								if (father != null && mother != null) {
									if (father.getFather() != -10 && father.getFather() == mother.getFather()
											|| father.getMother() != -10 && father.getMother() == mother.getMother()
											|| father.getWurmId() == mother.getFather()
											|| father.getMother() == mother.getWurmId())
									{
										performer.getCommunicator().sendNormalServerMessage("The " + mother.getName() + " and the " + father.getName() + " look very similar. You think they may be related.");
									}
								}
							}
							
							return method.invoke(proxy, args);
						}
					};
				}
			});
		} catch (NotFoundException e) {
			throw new HookException(e);
		}
	}

}
