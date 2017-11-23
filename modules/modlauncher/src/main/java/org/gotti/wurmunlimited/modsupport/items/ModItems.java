package org.gotti.wurmunlimited.modsupport.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gotti.wurmunlimited.modloader.callbacks.CallbackApi;
import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

import com.wurmonline.server.items.Item;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;

public class ModItems {

	/**
	 * The instance
	 */
	private static ModItems instance;

	/**
	 * model name providers per template id
	 */
	private Map<Integer, List<ModelNameProvider>> modelNameProviders = new HashMap<>();

	/**
	 * Constructor.
	 */
	private ModItems() {
		initHooks();
	}

	/**
	 * Initialize hooks
	 */
	private void initHooks() {
		try {
			final ClassPool classPool = HookManager.getInstance().getClassPool();
			final CtClass ctItem = classPool.get("com.wurmonline.server.items.Item");
			final CtMethod ctGetModelName = ctItem.getMethod("getModelName", Descriptor.ofMethod(classPool.get(String.class.getName()), new CtClass[0]));

			HookManager.getInstance().addCallback(ctItem, "moditems", this);

			ctGetModelName.insertBefore("{ String customModelName = moditems.getModelName($0); if (customModelName != null) { return customModelName; }}");
		} catch (NotFoundException | CannotCompileException e) {
			throw new HookException(e);
		}
	}

	/**
	 * Retrieve a custom model name for an item.
	 * 
	 * @param item
	 *            Item
	 * @return custom model name or null
	 */
	@CallbackApi
	public String getModelName(Item item) {
		// safety check
		if (item == null || item.getTemplate() == null) {
			return null;
		}

		// Check if there are ModelNameProviders for the item template
		List<ModelNameProvider> list = modelNameProviders.get(item.getTemplateId());
		if (list == null || list.isEmpty()) {
			return null;
		}

		// Ask the ModelNameProviders for a model name
		for (ModelNameProvider modelNameBuilder : list) {
			String modelName = modelNameBuilder.getModelName(item);
			if (modelName != null) {
				return modelName;
			}
		}
		return null;
	}

	/**
	 * Get the instance
	 * 
	 * @return the instance
	 */
	private static synchronized ModItems getInstance() {
		if (instance == null) {
			instance = new ModItems();
		}
		return instance;
	}

	/**
	 * Add a model name provider
	 * 
	 * @param templateId
	 *            Item template id
	 * @param modelNameProvider
	 *            Model name provider
	 */
	public static void addModelNameProvider(int templateId, ModelNameProvider modelNameProvider) {
		getInstance().modelNameProviders.computeIfAbsent(templateId, id -> new ArrayList<>()).add(modelNameProvider);
	}

	/**
	 * Initialize ModItems
	 */
	public static void init() {
		getInstance();
	}
}
