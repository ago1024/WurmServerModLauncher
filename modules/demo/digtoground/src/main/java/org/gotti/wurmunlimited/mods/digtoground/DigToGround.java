package org.gotti.wurmunlimited.mods.digtoground;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;


public class DigToGround implements WurmServerMod, PreInitable, Initable, Configurable {

	private static final Logger logger = Logger.getLogger(DigToGround.class.getName());
	
	boolean dredgeToShip = true;
	
	@Override
	public void configure(Properties properties) {
		this.dredgeToShip = Boolean.parseBoolean(properties.getProperty("dredgeToShip", String.valueOf(this.dredgeToShip)));
		
		logger.log(Level.INFO, "dredgeToShip: " + dredgeToShip);
	}
	
	@Override
	public void preInit() {
		
		try {
			ClassPool classpool = HookManager.getInstance().getClassPool();
			
			// static boolean dig(final Creature performer, final Item source, final int tilex, final int tiley, final int tile, final float counter, final MeshIO mesh) {
			String descriptor = Descriptor.ofMethod(CtClass.booleanType, new CtClass[] {
					classpool.get("com.wurmonline.server.creatures.Creature"),
					classpool.get("com.wurmonline.server.items.Item"),
					CtClass.intType,
					CtClass.intType,
					CtClass.intType,
					CtClass.floatType,
					CtClass.booleanType,
					classpool.get("com.wurmonline.mesh.MeshIO"),
					CtClass.booleanType,
			});
			classpool.get("com.wurmonline.server.behaviours.Terraforming").getMethod("dig", descriptor).instrument(new ExprEditor() {
				
				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					if (!dredgeToShip && "com.wurmonline.server.items.Item".equals(m.getClassName()) && m.getMethodName().equals("testInsertItem")) {
						
						// dredgeToShip = false disables dredging into the ship by always returning false from testInsertItem on the boat object

						String code =
								"if ($0.isBoat()) {\n" +
								"  $_ = false;" +
								"} else {\n" +
								"  $_ = $proceed($$);\n" +
								"}\n";
						m.replace(code);
					} else if ("com.wurmonline.server.items.Item".equals(m.getClassName()) && m.getMethodName().equals("insertItem")) {
						
						// We check if the item to be inserted is the dug up item (dirt, clay, sand...)
						// and if we are not inserting the item into a boat. Dredging into the boat is either
						// allowed anyway or prevented by overwriting testInsertItem above

						String code =
								"if ($1 != null && $1.getTemplateId() == createdItemTemplate && !$0.isBoat()) {\n" +
								"  $1.putItemInfrontof(performer);" +
								"  $_ = true;" +
								"} else {\n" +
								"  $_ = $proceed($$);\n" +
								"}\n";
						m.replace(code);
					} else if ("com.wurmonline.server.Server".equals(m.getClassName()) && m.getMethodName().equals("isDirtHeightLower")) {
						// After isDirtHeightLower the gem and mission items are handled
						//replaceInsertItem = false;
					} else if ("com.wurmonline.server.items.Item".equals(m.getClassName()) && m.getMethodName().equals("getNumItemsNotCoins")) {
						m.replace("$_ = 0;");
					} else if ("com.wurmonline.server.creatures.Creature".equals(m.getClassName()) && m.getMethodName().equals("canCarry")) {
						m.replace("$_ = true;");
					} else if ("com.wurmonline.server.items.Item".equals(m.getClassName()) && m.getMethodName().equals("getFreeVolume")) {
						m.replace("$_ = 1000;");
					}
				}
			});
			
			//private static final void getDirt(final Creature performer, final int x, final int y, final int maxDiff, final int preferredHeight, final boolean quickLevel) {
			descriptor = Descriptor.ofMethod(CtClass.voidType, new CtClass[] {
					classpool.get("com.wurmonline.server.creatures.Creature"),
					classpool.get("com.wurmonline.server.items.Item"),
					CtClass.intType,
					CtClass.intType,
					CtClass.intType,
					CtClass.intType,
					CtClass.booleanType,
					classpool.get("com.wurmonline.server.behaviours.Action")
			});
			classpool.get("com.wurmonline.server.behaviours.Flattening").getMethod("getDirt", descriptor).instrument(new ExprEditor() {
				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					if ("com.wurmonline.server.items.Item".equals(m.getClassName()) && m.getMethodName().equals("insertItem")) {
						m.replace("{ dirt.putItemInfrontof(performer); $_ = true; }");
					} else if ("com.wurmonline.server.items.Item".equals(m.getClassName()) && m.getMethodName().equals("isDredgingTool")) {
						m.replace("$_ = $proceed($$) && $0.getTemplateId() != 315 && $0.getTemplateId() != 176;");
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
}
