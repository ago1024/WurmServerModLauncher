package org.gotti.wurmunlimited.modsupport.items;

import org.gotti.wurmunlimited.modsupport.IdType;
import org.gotti.wurmunlimited.modsupport.NonFreezingNamedIdParser;

/**
 * Parse a list of item names and ids.
 */
public class ItemIdParser extends NonFreezingNamedIdParser {
	
	@Override
	protected String getNamesClassName() {
		return "com.wurmonline.server.items.ItemList";
	}
	
	@Override
	protected IdType getIdFactoryType() {
		return IdType.CREATURETEMPLATE;
	}
		
	@Override
	protected int unparsable(String name) {
		throw new IllegalArgumentException(name + " is not a valid item name");
	}
}
