package org.gotti.wurmunlimited.modsupport.actions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.behaviours.Behaviour;
import com.wurmonline.server.behaviours.WrappedBehaviourProvider;

public class ModActions {
	
	private static boolean inited = false;
	
	private static List<BehaviourProvider> behaviourProviders = new LinkedList<>();
	private static Map<Short, ActionPerformer> actionPerformers = new HashMap<>();
	
	public static int getNextActionId() {
		return Actions.actionEntrys.length;
	}
	
	public static void registerAction(ActionEntry actionEntry) {
		
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
		ActionPerformer actionperformer = testAction.getActionPerformer();
		if (actionperformer != null) {
			short actionId = actionperformer.getActionId();
			actionPerformers.put(actionId, actionperformer);
		}
		
		BehaviourProvider behaviourProvider = testAction.getBehaviourProvider();
		if (!behaviourProviders.contains(behaviourProvider)) {
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
			for (CtMethod method : ctBehaviourDispatcher.getMethods()) {
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
						code.append("    org.gotti.wurmunlimited.modsupport.actions.ActionPerformer actionPerformer = org.gotti.wurmunlimited.modsupport.actions.ModActions.getActionPerformer(this);\n");
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
	
	public static ActionPerformer getActionPerformer(Action action) {
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
