package org.gotti.wurmunlimited.mods.digtoground;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;

public class DigToGround implements WurmMod, PreInitable, Initable {
	
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
				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					if ("com.wurmonline.server.items.Item".equals(m.getClassName()) && m.getMethodName().equals("insertItem")) {
						m.replace("{ created.putItemInfrontof(performer); $_ = true; }");
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
