package org.gotti.wurmunlimited.modsupport.items;

import org.gotti.wurmunlimited.modsupport.NamedIdParser;

import com.wurmonline.server.items.ItemList;

/**
 * Parse a list of item names and ids.
 */
public class ItemIdParser extends NamedIdParser {
	
	@Override
	protected Class<?> getNamesClass() {
		return ItemList.class;
	}
	
	@Override
	protected int unparsable(String name) {
		throw new IllegalArgumentException(name + "is not a valid item name");
	}
}
