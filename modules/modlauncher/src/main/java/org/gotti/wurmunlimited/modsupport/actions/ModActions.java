package org.gotti.wurmunlimited.modsupport.actions;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.behaviours.Behaviour;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class ModActions {
	
	private static boolean inited = false;
	
	private static short lastServerActionId = 0;
	
	private static List<BehaviourProvider> behaviourProviders = new CopyOnWriteArrayList<>();
	private static ConcurrentHashMap<Short, ActionPerformerChain> actionPerformers = new ConcurrentHashMap<>();
	
	public static int getNextActionId() {
		return Actions.actionEntrys.length;
	}
	
	private static void initLastServerActionId() {
		if (lastServerActionId == 0) {
			lastServerActionId = (short) (Actions.actionEntrys.length - 1);
		}
	}
	
	public static short getLastServerActionId() {
		initLastServerActionId();
		return lastServerActionId;
	}
	
	public static void registerAction(ActionEntry actionEntry) {
		
		initLastServerActionId();
		
		short number = actionEntry.getNumber();
		
		if (Actions.actionEntrys.length != number) {
			throw new RuntimeException(String.format("Trying to register an action with the wrong action number. Expected %d, got %d", Actions.actionEntrys.length, number));
		}
		
		ActionEntry[] newArray = Arrays.copyOf(Actions.actionEntrys, number + 1);
		newArray[number] = actionEntry;
		
		try {
			ReflectionUtil.setPrivateField(Actions.class, ReflectionUtil.getField(Actions.class, "actionEntrys"), newArray);
		} catch (IllegalAccessException | IllegalArgumentException | ClassCastException | NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void registerAction(ModAction testAction) {
		registerActionPerformer(testAction.getActionPerformer());
		
		registerBehaviourProvider(testAction.getBehaviourProvider());
	}
	
	public static void registerActionPerformer(ActionPerformer actionPerformer) {
		if (actionPerformer != null) {
			short actionId = actionPerformer.getActionId();
			actionPerformers.computeIfAbsent(actionId, num -> new ActionPerformerChain(num)).addActionPerformer(actionPerformer);
		}
	}
	
	public static void registerBehaviourProvider(BehaviourProvider behaviourProvider) {
		if (behaviourProvider != null && !behaviourProviders.contains(behaviourProvider)) {
			behaviourProviders.add(behaviourProvider);
		}
	}

	public static void init() {
		if (inited)
			return;
		
		try {
			final ClassPool classPool = HookManager.getInstance().getClassPool();
			
			CtClass ctActions = classPool.get("com.wurmonline.server.behaviours.Actions");
			CtField ctActionEntrys = ctActions.getField("actionEntrys");
			ctActionEntrys.setModifiers(Modifier.clear(ctActionEntrys.getModifiers(), Modifier.FINAL));
			
			CtClass ctBehaviourDispatcher  = classPool.get("com.wurmonline.server.behaviours.BehaviourDispatcher");
			for (CtMethod method : ctBehaviourDispatcher.getDeclaredMethods()) {
				method.instrument(new ExprEditor() {
					@Override
					public void edit(MethodCall m) throws CannotCompileException {
						if (m.getClassName().equals("com.wurmonline.server.behaviours.Behaviour") && m.getMethodName().equals("getBehavioursFor")) {
							StringBuffer code = new StringBuffer();
							code.append("{\n");
							code.append("    org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider behaviourProvider = org.gotti.wurmunlimited.modsupport.actions.ModActions.getBehaviourProvider($0);\n");
							code.append("    if (behaviourProvider != null) {\n");
							code.append("        $_ = behaviourProvider.getBehavioursFor($$);\n");
							code.append("    } else {\n");
							code.append("        $_ = $proceed($$);\n");
							code.append("    }\n");
							code.append("}\n");
							m.replace(code.toString());
						}
					}
				});
			}
			
			classPool.get("com.wurmonline.server.behaviours.Action").getMethod("poll", "()Z").instrument(new ExprEditor() {
				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					if (m.getClassName().equals("com.wurmonline.server.behaviours.Behaviour") && m.getMethodName().equals("action")) {
						StringBuffer code = new StringBuffer();
						code.append("{\n");
						code.append("    org.gotti.wurmunlimited.modsupport.actions.ActionPerformerBase actionPerformer = org.gotti.wurmunlimited.modsupport.actions.ModActions.getActionPerformer(this);\n");
						code.append("    if (actionPerformer != null) {\n");
						code.append("        $_ = actionPerformer.action($$);\n");
						code.append("    } else {\n");
						code.append("        $_ = $proceed($$);\n");
						code.append("    }\n");
						code.append("}\n");
						m.replace(code.toString());
					}
				}
			});
			
			
			
			inited = true;
		} catch (NotFoundException | CannotCompileException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static ActionPerformerBase getActionPerformer(Action action) {
		short actionId = action.getActionEntry().getNumber();
		return actionPerformers.get(actionId);
	}
	
	public static BehaviourProvider getBehaviourProvider(Behaviour behaviour) {
		if (behaviourProviders == null || behaviourProviders.isEmpty()) {
			return null;
		}
		
		return new ChainedBehaviourProvider(new WrappedBehaviourProvider(behaviour), behaviourProviders);
	}
}
