package org.gotti.wurmunlimited.mods.christmasmod;


import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.Descriptor;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import org.gotti.wurmunlimited.modloader.classhooks.CodeReplacer;
import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;

public class ChristmasMod implements WurmMod, PreInitable, Configurable {
	
	int present2015 = 972;
	
	@Override
	public void configure(Properties properties) {
		present2015 = Integer.valueOf(properties.getProperty("present2015", String.valueOf(present2015)));
		
		Logger.getLogger(ChristmasMod.class.getName()).log(Level.INFO, "present2015: " + present2015);
	}

	@Override
	public void preInit() {

		try {
			ClassPool classPool = HookManager.getInstance().getClassPool();

			CtClass ctWurmCalendar = classPool.get("com.wurmonline.server.WurmCalendar");

			// com.wurmonline.server.WurmCalendar.isChristmas()
			ctWurmCalendar.getMethod("isChristmas", "()Z").setBody("return nowIsBetween(17, 0, 23, 11, java.time.Year.now().getValue(), 6, 0, 29, 11, java.time.Year.now().getValue());");
			
			// com.wurmonline.server.WurmCalendar.isBeforeChristmas()
			ctWurmCalendar.getMethod("isBeforeChristmas", "()Z").setBody("return false;");
			
			// com.wurmonline.server.WurmCalendar.isAfterChristmas()
			ctWurmCalendar.getMethod("isAfterChristmas", "()Z").setBody("return nowIsAfter(6, 0, 29, 11, java.time.Year.now().getValue());");
			
			// boolean action(final Action act, final Creature performer, final Item target, final short action, final float counter) {
			CtClass[] parameterTypes = new CtClass[] {
					classPool.get("com.wurmonline.server.behaviours.Action"),
					classPool.get("com.wurmonline.server.creatures.Creature"),
					classPool.get("com.wurmonline.server.items.Item"),
					classPool.get("short"),
					classPool.get("float")
			};
			CtMethod action = classPool.get("com.wurmonline.server.behaviours.ItemBehaviour").getMethod("action", Descriptor.ofMethod(CtClass.booleanType, parameterTypes));
			
			Bytecode bytecode = new Bytecode(action.getMethodInfo().getConstPool());
			bytecode.add(Bytecode.ALOAD_3);
			bytecode.addInvokevirtual(classPool.get("com.wurmonline.server.items.Item"), "getAuxData", "()B");
			bytecode.add(Bytecode.BIPUSH);
			bytecode.add(7);
			bytecode.add(Bytecode.IF_ICMPNE);
			bytecode.add(0, 13);
			bytecode.add(Bytecode.SIPUSH);
			bytecode.add(972 >> 8, 972 & 0xff);
			byte[] search = bytecode.get();
			
			bytecode = new Bytecode(action.getMethodInfo().getConstPool());
			bytecode.add(Bytecode.ALOAD_3);
			bytecode.addInvokevirtual(classPool.get("com.wurmonline.server.items.Item"), "getAuxData", "()B");
			bytecode.add(Bytecode.BIPUSH);
			bytecode.add(8);
			bytecode.add(Bytecode.IF_ICMPNE);
			bytecode.add(0, 13);
			bytecode.add(Bytecode.SIPUSH);
			bytecode.add(present2015 >> 8, present2015 & 0xff);
			byte[] replace = bytecode.get();
			
			new CodeReplacer(action.getMethodInfo().getCodeAttribute()).replaceCode(search, replace);
			
			// com.wurmonline.server.behaviours.ItemBehaviour.awardChristmasPresent(Creature)
			classPool.get("com.wurmonline.server.behaviours.ItemBehaviour").getMethod("awardChristmasPresent", Descriptor.ofMethod(CtClass.voidType, new CtClass[] { classPool.get("com.wurmonline.server.creatures.Creature") })).instrument(new ExprEditor() {
				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					// com.wurmonline.server.items.Item.setAuxData(byte)
					if (m.getClassName().equals("com.wurmonline.server.items.Item") && m.getMethodName().equals("setAuxData")) {
						StringBuffer code = new StringBuffer();
						code.append("if (java.time.Year.now().getValue() > 2007) {\n");
						code.append("    $_ = $proceed((byte)(java.time.Year.now().getValue() - 2007));\n");
						code.append("} else {\n");
						code.append("    $_ = $proceed($$);\n");
						code.append("}");
						m.replace(code.toString());
					}
				}
			});
			
			classPool.get("com.wurmonline.server.players.Player").getMethod("reimburse", "()V").instrument(new ExprEditor() {
				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					// com.wurmonline.server.players.PlayerInfo.setReimbursed(boolean)
					if (m.getClassName().equals("com.wurmonline.server.players.PlayerInfo") && m.getMethodName().equals("setReimbursed")) {
						m.replace("{};");
					}
				}
			});
			
		} catch (NotFoundException | CannotCompileException | BadBytecode e) {
			throw new HookException(e);
		}
	}

}
