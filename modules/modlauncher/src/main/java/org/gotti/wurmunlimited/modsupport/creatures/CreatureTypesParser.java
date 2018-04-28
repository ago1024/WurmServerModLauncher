package org.gotti.wurmunlimited.modsupport.creatures;

import org.gotti.wurmunlimited.modsupport.NonFreezingNamedIdParser;

/**
 * Parse a list of creature types and creature type ids.
 */
public class CreatureTypesParser extends NonFreezingNamedIdParser {
	
	@Override
	protected String getNamesClassName() {
		return "com.wurmonline.shared.constants.CreatureTypes";
	}
	
	@Override
	protected boolean isValidName(String fieldName) {
		return fieldName.startsWith("C_TYPE");
	}
	
	@Override
	protected String cleanupFieldName(String fieldName) {
		return fieldName.replaceAll("^C_TYPE_", "");
	}
	
	@Override
	protected int unparsable(String name) {
		throw new IllegalArgumentException(name + " is not a valid creature type");
	}
}
