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
		this.dredgeToShip = Boolean.parseBoolean(properties.getProperty("dregeToShip", String.valueOf(this.dredgeToShip)));
		
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
					classpool.get("com.wurmonline.mesh.MeshIO")
			});
			classpool.get("com.wurmonline.server.behaviours.Terraforming").getMethod("dig", descriptor).instrument(new ExprEditor() {
				
				int i = 0;
				
				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					if ("com.wurmonline.server.items.Item".equals(m.getClassName()) && m.getMethodName().equals("insertItem")) {
						/* Only replace the first two occurances of insert item */
						if (i >= 2)
							return;
						i++;
						
						StringBuffer buffer = new StringBuffer();
						if (dredgeToShip) {
							buffer.append("{");
							buffer.append("	com.wurmonline.server.items.Item v = dredging && performer.getVehicle() != -10 ? com.wurmonline.server.Items.getItem(performer.getVehicle()) : null;");
							buffer.append("	if (v != null && v.isHollow() && v.getNumItemsNotCoins() < 100 && v.getFreeVolume() >= created.getVolume()) {");
							buffer.append("		v.insertItem(created, true);");
							buffer.append("	} else if (v != null && v.isHollow()) {");
							buffer.append("		created.putItemInfrontof(performer);");
							buffer.append("		performer.getCommunicator().sendNormalServerMessage(\"The \" + v.getName() + \" is full and the \" + created.getName() + \" flows to the ground.\");");
							buffer.append("	} else {");
							buffer.append("		created.putItemInfrontof(performer);");
							buffer.append("	}");
							buffer.append("	$_ = true;");
							buffer.append("}");
						} else {
							buffer.append("{");
							buffer.append("	created.putItemInfrontof(performer);");
							buffer.append("	$_ = true;");
							buffer.append("}");
						}
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
					CtClass.intType,
					CtClass.intType,
					CtClass.intType,
					CtClass.intType,
					CtClass.booleanType
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
