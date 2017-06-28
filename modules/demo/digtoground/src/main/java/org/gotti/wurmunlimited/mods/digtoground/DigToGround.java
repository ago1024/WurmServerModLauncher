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

	private static Logger logger = Logger.getLogger(DigToGround.class.getName());
	
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
					classpool.get("com.wurmonline.mesh.MeshIO")
			});
			classpool.get("com.wurmonline.server.behaviours.Terraforming").getMethod("dig", descriptor).instrument(new ExprEditor() {
				
				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					if (!dredgeToShip && "com.wurmonline.server.items.Item".equals(m.getClassName()) && m.getMethodName().equals("testInsertItem")) {
						
						// dredgeToShip = false disables dredging into the ship by always returning false from testInsertItem on the boat object
						
						StringBuffer buffer = new StringBuffer();
						buffer.append("if ($0.isBoat()) {\n");
						buffer.append("  $_ = false;");
						buffer.append("} else {\n");
						buffer.append("  $_ = $proceed($$);\n");
						buffer.append("}\n");
						m.replace(buffer.toString());
					} else if ("com.wurmonline.server.items.Item".equals(m.getClassName()) && m.getMethodName().equals("insertItem")) {
						
						// We check if the item to be inserted is the dug up item (dirt, clay, sand...)
						// and if we are not inserting the item into a boat. Dredging into the boat is either
						// allowed anyway or prevented by overwriting testInsertItem above
						
						StringBuffer buffer = new StringBuffer();
						buffer.append("if ($1 == created && !$0.isBoat()) {\n");
						buffer.append("  created.putItemInfrontof(performer);");
						buffer.append("  $_ = true;");
						buffer.append("} else {\n");
						buffer.append("  $_ = $proceed($$);\n");
						buffer.append("}\n");
						m.replace(buffer.toString());
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
