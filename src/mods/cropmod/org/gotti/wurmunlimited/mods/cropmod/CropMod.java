package org.gotti.wurmunlimited.mods.cropmod;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javassist.CannotCompileException;
import javassist.ClassPool;
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
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
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
			ClassPool classPool = HookManager.getInstance().getClassPool();
			CtClass terraForming = classPool.get("com.wurmonline.server.behaviours.Terraforming");

			CtClass[] paramTypes = {
					classPool.get("com.wurmonline.server.creatures.Creature"),
					CtPrimitiveType.intType,
					CtPrimitiveType.intType,
					CtPrimitiveType.booleanType,
					CtPrimitiveType.intType,
					CtPrimitiveType.floatType,
					classPool.get("com.wurmonline.server.items.Item")
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
				CtClass ctCropTilePoller = HookManager.getInstance().getClassPool().get("com.wurmonline.server.zones.CropTilePoller");
				// pollCropTiles is where the server runs through all the fields to call checkForFarmGrowth on them.
				// But it keeps all the tiles in one list. We get that method...
				CtMethod ctPollCropTiles = ctCropTilePoller.getDeclaredMethod("pollCropTiles");
				// Now to employ a new ExprEditor that marks tiles of age 6 as for removal, and skips the
				// checkForFarmGrowth call. If it's not age 6, continue as normal.
				ctPollCropTiles.instrument(new ExprEditor() {
					public void edit(MethodCall methodCall) throws CannotCompileException {
						if (methodCall.getClassName().equals("com.wurmonline.server.zones.CropTilePoller") && methodCall.getMethodName().equals("checkForFarmGrowth")) {
							StringBuffer code = new StringBuffer();
							code.append("if (((data >> 4) & 0x7) == 6) {\n");
							code.append("	toRemove.add(cTile);\n");
							code.append("	$_ = null;\n");
							code.append("} else {\n");
							code.append("	$_ = $proceed($$);\n");
							code.append("}\n");
							methodCall.replace(code.toString());
						}
					}
				});
			} catch (NotFoundException | CannotCompileException e) {
				throw new HookException(e);
			}
			
			try {
				// com.wurmonline.server.zones.TilePoller.checkForFarmGrowth(int, int, int, byte, byte)
				CtClass ctTilePoller = HookManager.getInstance().getClassPool().get("com.wurmonline.server.zones.TilePoller");
				CtMethod ctCheckEffects = ctTilePoller.getDeclaredMethod("checkEffects");
				// Now to employ a new ExprEditor that skips tiles of age 6. If it's not age 6, continue as normal.
				ctCheckEffects.instrument(new ExprEditor() {
					public void edit(MethodCall methodCall) throws CannotCompileException {
						if (methodCall.getClassName().equals("com.wurmonline.server.zones.TilePoller") && methodCall.getMethodName().equals("checkForFarmGrowth")) {
							StringBuffer code = new StringBuffer();
							code.append("if ((($5 >> 4) & 0x7) != 6) {\n");
							code.append("	$_ = $proceed($$);\n");
							code.append("}\n");
							methodCall.replace(code.toString());
						}
					}
				});
			} catch (NotFoundException | CannotCompileException e) {
				throw new HookException(e);
			}
			
		}
	}
}
