package org.gotti.wurmunlimited.modsupport.vehicles;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;

import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.classhooks.InvocationHandlerFactory;

import com.wurmonline.server.behaviours.Vehicle;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;

public class ModVehicleBehaviours {

	private static Map<Integer, ModVehicleBehaviour> itemVehicles = null;
	private static Map<Integer, ModVehicleBehaviour> creatureVehicles = null;
	private static boolean inited = false;

	public static void init() {
		if (inited)
			return;
		
		itemVehicles = new HashMap<>();
		creatureVehicles = new HashMap<>();

		try {

			ClassPool classpool = HookManager.getInstance().getClassPool();
			String descriptor = Descriptor.ofMethod(CtClass.voidType, new CtClass[] { classpool.get("com.wurmonline.server.creatures.Creature"), classpool.get("com.wurmonline.server.behaviours.Vehicle") });
			HookManager.getInstance().registerHook("com.wurmonline.server.behaviours.Vehicles", "setSettingsForVehicle", descriptor, new InvocationHandlerFactory() {

				@Override
				public InvocationHandler createInvocationHandler() {
					return new InvocationHandler() {

						@Override
						public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
							Object result = method.invoke(proxy, args);

							Creature creature = (Creature) args[0];
							ModVehicleBehaviour vehicle = creatureVehicles.get(creature.getTemplate().getTemplateId());
							if (vehicle != null) {
								vehicle.setSettingsForVehicle(creature, (Vehicle) args[1]);
							}

							return result;
						}
					};
				}
			});

			descriptor = Descriptor.ofMethod(CtClass.voidType, new CtClass[] { classpool.get("com.wurmonline.server.items.Item"), classpool.get("com.wurmonline.server.behaviours.Vehicle") });
			HookManager.getInstance().registerHook("com.wurmonline.server.behaviours.Vehicles", "setSettingsForVehicle", descriptor, new InvocationHandlerFactory() {

				@Override
				public InvocationHandler createInvocationHandler() {
					return new InvocationHandler() {

						@Override
						public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
							Object result = method.invoke(proxy, args);

							Item item = (Item) args[0];
							ModVehicleBehaviour vehicle = itemVehicles.get(item.getTemplate().getTemplateId());
							if (vehicle != null) {
								vehicle.setSettingsForVehicle(item, (Vehicle) args[1]);
							}

							return result;
						}
					};
				}
			});

		} catch (NotFoundException e) {
			throw new RuntimeException(e);
		}
		
		inited = true;
	}

	public static void addCreatureVehicle(int creatureTemplateId, ModVehicleBehaviour vehicle) {
		if (!inited) {
			throw new RuntimeException("ModVehicles was not initialized");
		}
		creatureVehicles.put(creatureTemplateId, vehicle);
	}

	public static void addItemVehicle(int itemTemplateId, ModVehicleBehaviour vehicle) {
		if (!inited) {
			throw new RuntimeException("ModVehicles was not initialized");
		}
		itemVehicles.put(itemTemplateId, vehicle);
	}
}
