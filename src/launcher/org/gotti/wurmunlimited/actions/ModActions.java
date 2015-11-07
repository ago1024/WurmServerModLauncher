package org.gotti.wurmunlimited.actions;

import java.util.Arrays;

import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.NotFoundException;

import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.Actions;

public class ModActions {
	
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

	public static void init() {
		try {
			CtClass ctActions = HookManager.getInstance().getClassPool().get("com.wurmonline.server.behaviours.Actions");
			CtField ctActionEntrys = ctActions.getField("actionEntrys");
			ctActionEntrys.setModifiers(Modifier.clear(ctActionEntrys.getModifiers(), Modifier.FINAL));
		} catch (NotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
