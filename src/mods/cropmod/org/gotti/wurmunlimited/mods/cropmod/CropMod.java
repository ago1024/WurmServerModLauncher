package org.gotti.wurmunlimited.mods.cropmod;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtPrimitiveType;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.Descriptor;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.classhooks.InvocationHandlerFactory;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;

public class CropMod implements WurmMod, Configurable, Initable, PreInitable {

	private boolean disableWeeds = true;
	private int extraHarvest = 0;
	private Logger logger = Logger.getLogger(this.getClass().getName());


	//
	// The method configure is called when the mod is being loaded
	//
	@Override
	public void configure(Properties properties) {

		disableWeeds = Boolean.valueOf(properties.getProperty("disableWeeds", Boolean.toString(disableWeeds)));
		extraHarvest = Integer.valueOf(properties.getProperty("extraHarvest", Integer.toString(extraHarvest)));
		logger.log(Level.INFO, "disableWeeds: " + disableWeeds);
		logger.log(Level.INFO, "extraHarvest: " + extraHarvest);
	}
	
	@Override
	public void preInit() {
		if (extraHarvest > 0) {
			initExtraHarvest();
		}
		
	}
	
	private void initExtraHarvest() {
		try {
			CtClass terraForming = HookManager.getInstance().getClassPool().get("com.wurmonline.server.behaviours.Terraforming");

			CtClass[] paramTypes = {
					HookManager.getInstance().getClassPool().get("com.wurmonline.server.creatures.Creature"),
					CtPrimitiveType.intType,
					CtPrimitiveType.intType,
					CtPrimitiveType.booleanType,
					CtPrimitiveType.intType,
					CtPrimitiveType.floatType,
					HookManager.getInstance().getClassPool().get("com.wurmonline.server.items.Item")
			};
			
			CtMethod method = terraForming.getMethod("harvest", Descriptor.ofMethod(CtPrimitiveType.booleanType, paramTypes));
			MethodInfo methodInfo = method.getMethodInfo();
			CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
			CodeIterator codeIterator = codeAttribute.iterator();
			
			LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
			int quantityIndex = -1;
			for (int i = 0; i < attr.tableLength(); i++) {
				if ("quantity".equals(attr.variableName(i))) {
					quantityIndex = attr.index(i);
				}
			}
			
			if (quantityIndex == -1) {
				throw new HookException("Quantity variable can not be resolved");
			}
			
			while (codeIterator.hasNext()) {
				int pos = codeIterator.next();
				int op = codeIterator.byteAt(pos);
				if (op == CodeIterator.ISTORE) {
					int fieldRefIdx = codeIterator.byteAt(pos + 1);
					if (quantityIndex == fieldRefIdx) {
						Bytecode bytecode = new Bytecode(codeIterator.get().getConstPool());
						bytecode.addIconst(extraHarvest);
						bytecode.add(Bytecode.IADD);
						codeIterator.insertAt(pos, bytecode.get());
						break;
					}
				}
			}
		} catch (NotFoundException | BadBytecode e) {
			throw new HookException(e);
		}
	}
	
	@Override
	public void init() {
		//
		// We initialize a method hook that gets called right before CropTilePoller.checkForFarmGrowth is called
		//
		if (disableWeeds) {
			try {

				//
				// To make sure we hook the correct method the list of method parameter types is compiled
				//
				CtClass[] paramTypes = {
						CtPrimitiveType.intType,
						CtPrimitiveType.intType,
						CtPrimitiveType.intType,
						CtPrimitiveType.byteType,
						CtPrimitiveType.byteType,
						HookManager.getInstance().getClassPool().get("com.wurmonline.mesh.MeshIO"),
						CtPrimitiveType.booleanType
				};
				
				//
				// next we register the hook for 
				// com.wurmonline.server.zones.CropTilePoller.checkForFarmGrowth(int, int, int, byte, byte, MeshIO, boolean) 
				//
				HookManager.getInstance().registerHook("com.wurmonline.server.zones.CropTilePoller", "checkForFarmGrowth", Descriptor.ofMethod(CtPrimitiveType.voidType, paramTypes), new InvocationHandlerFactory() {

					@Override
					public InvocationHandler createInvocationHandler() {
						return new InvocationHandler() {

							//
							// The actual hook is an InvocationHandler. It's invoke method is called instead of the hooked method.
							// The object, method and arguments are passed as parameters to invoke()
							//
							@Override
							public Object invoke(Object object, Method method, Object[] args) throws Throwable {
								//
								// When the hook is called we can do stuff depending on the input parameters
								// Here we check if the tileAge is 6 (the second ripe stage)
								//
								byte aData = ((Number) args[4]).byteValue();
								final int tileState = aData >> 4;
								int tileAge = tileState & 0x7;
								if (tileAge == 6) {
									// tileAge is 6. Advancing it further would create weeds.
									// Therefore we just exit here.
									// return null is required if the hooked method has a void return type
									return null;
								}

								//
								// tileAge is not 6. We just continue by calling the hooked method
								//
								return method.invoke(object, args);
							}
						};
					}
				});
			} catch (NotFoundException e) {
				throw new HookException(e);
			}
		}
	}
}
