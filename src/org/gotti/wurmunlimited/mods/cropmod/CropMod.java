package org.gotti.wurmunlimited.mods.cropmod;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javassist.CtClass;
import javassist.CtPrimitiveType;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;

import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;

public class CropMod implements WurmMod, Configurable {

	private boolean disableWeeds = true;
	private Logger logger = Logger.getLogger(this.getClass().getName());


	@Override
	public void configure(Properties properties) {

		disableWeeds = Boolean.valueOf(properties.getProperty("disableWeeds", Boolean.toString(disableWeeds)));
		
		logger.log(Level.INFO, "disableWeeds: " + disableWeeds);

		if (disableWeeds) {
			try {
				CtClass[] paramTypes = {
						CtPrimitiveType.intType,
						CtPrimitiveType.intType,
						CtPrimitiveType.intType,
						CtPrimitiveType.byteType,
						CtPrimitiveType.byteType,
						HookManager.getInstance().getClassPool().get("com.wurmonline.mesh.MeshIO"),
						CtPrimitiveType.booleanType
				};
				
				HookManager.getInstance().registerHook("com.wurmonline.server.zones.CropTilePoller", "checkForFarmGrowth", Descriptor.ofMethod(CtPrimitiveType.voidType, paramTypes), new InvocationHandler() {
	
					@Override
					public Object invoke(Object object, Method method, Object[] args) throws Throwable {
						byte aData = ((Number)args[4]).byteValue();
						final int tileState = aData >> 4;
						int tileAge = tileState & 0x7;
						if (tileAge == 6)
							return null;
	
						return method.invoke(object, args);
					}
				});
			} catch (NotFoundException e) {
				throw new HookException(e);
			}
		}
	}
}
