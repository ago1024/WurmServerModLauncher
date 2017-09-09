package org.gotti.wurmunlimited.modsupport.actions;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.HookException;

import com.wurmonline.server.behaviours.ActionEntry;

/**
 * Builder for ActionEntries
 */
public class ActionEntryBuilder {

	private static Constructor<ActionEntry> constructor;

	private short number;

	private int priority;

	private String actionString;

	private String verbString;

	private String animationString;

	private int[] types;

	private int range;

	private boolean blockedByUseOnGroundOnly;

	/**
	 * Internal constructor. Preset values from public constructors.
	 */
	private ActionEntryBuilder(short aNumber, int aPriority, String aActionString, String aVerbString, String aAnimationString, int[] aTypes, int aRange, boolean blockedByUseOnGroundOnly) {
		if (constructor == null) {
			throw new HookException("ActionEntryBuilder should be called after the server started");
		}
		
		number(aNumber);
		priority(aPriority);
		actionString(aActionString);
		verbString(aVerbString);
		animationString(aActionString);
		types(aTypes);
		range(aRange);
		blockedByUseOnGroundOnly(blockedByUseOnGroundOnly);
	}

	/**
	 * Create builder with preset number, action and verb string.
	 * 
	 * @param number Action number
	 * @param actionString Action name (i.e. Dig)
	 * @param verbString Action verb (i.e. digging)
	 */
	public ActionEntryBuilder(short number, String actionString, String verbString) {
		this(number, 5, actionString, verbString, actionString, (int[]) null, 4, true);
	}

	/**
	 * Create builder with preset number, types, action and verb string.
	 * 
	 * @param number Action number
	 * @param actionString Action name (i.e. Dig)
	 * @param verbString Action verb (i.e. digging)
	 * @param types Action types. See {@link ActionTypes}
	 */
	public ActionEntryBuilder(short number, String actionString, String verbString, int[] types) {
		this(number, 5, actionString, verbString, actionString, types, 2, true);
	}

	/**
	 * Set the action number.
	 */
	private ActionEntryBuilder number(short number) {
		this.number = number;
		return this;
	}

	/**
	 * Set the priority.
	 */
	public ActionEntryBuilder priority(int priority) {
		this.priority = priority;
		return this;
	}

	/**
	 * Set the action name. I.e. "Dig"
	 */
	public ActionEntryBuilder actionString(String actionString) {
		this.actionString = actionString;
		return this;
	}

	/**
	 * Set the action verb. I.e. "digging"
	 */
	public ActionEntryBuilder verbString(String verbString) {
		this.verbString = verbString;
		return this;
	}

	/**
	 * Set the animation string if different from the action verb.
	 */
	public ActionEntryBuilder animationString(String animationString) {
		this.animationString = animationString;
		return this;
	}

	/**
	 * Set action types. See {@link ActionTypes}.
	 */
	public ActionEntryBuilder types(int[] types) {
		this.types = types;
		return this;
	}

	/**
	 * Set the range.
	 */
	public ActionEntryBuilder range(int range) {
		this.range = range;
		return this;
	}

	/**
	 * Set blocked by use on ground.
	 */
	public ActionEntryBuilder blockedByUseOnGroundOnly(boolean blockedByUseOnGroundOnly) {
		this.blockedByUseOnGroundOnly = blockedByUseOnGroundOnly;
		return this;
	}

	/**
	 * Build the ActionEntry
	 */
	public ActionEntry build() {
		try {
			ActionEntry actionEntry = ReflectionUtil.callPrivateConstructor(constructor, number, priority, actionString, verbString, animationString, types, range, blockedByUseOnGroundOnly);
			return actionEntry;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new HookException(e);
		}
	}

	/**
	 * Resolve ActionEntry constructor
	 */
	public static void init() {
		Class<?> parameterTypes[] = {
				short.class /* number */,
				int.class /* priority */,
				String.class /* actionString */,
				String.class /* verbString */,
				String.class /* animationString */,
				int[].class /* types */,
				int.class /* range */,
				boolean.class /* blockedByUseOnGroud */
		};
		Class<ActionEntry> clazz = ActionEntry.class;

		try {
			ActionEntryBuilder.constructor = clazz.getDeclaredConstructor(parameterTypes);
		} catch (NoSuchMethodException e) {
			throw new HookException(e);
		}
	}
}
