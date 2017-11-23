package org.gotti.wurmunlimited.modsupport.items;

import com.wurmonline.server.items.Item;

/**
 * Provide a custom model name for an item
 * 
 * @author ago
 */
public interface ModelNameProvider {

	/**
	 * Get the model name for an item
	 * 
	 * @param item
	 *            Item
	 * @return model name or null if no custom model name is wanted
	 */
	String getModelName(Item item);

}
