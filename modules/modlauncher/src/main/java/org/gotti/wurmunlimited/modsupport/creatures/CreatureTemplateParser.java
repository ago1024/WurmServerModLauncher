package org.gotti.wurmunlimited.modsupport.creatures;

import org.gotti.wurmunlimited.modsupport.IdType;
import org.gotti.wurmunlimited.modsupport.NonFreezingNamedIdParser;

/**
 * Parse a list of creature templates and creature template ids.
 */
public class CreatureTemplateParser extends NonFreezingNamedIdParser {
	
	@Override
	protected String getNamesClassName() {
		return "com.wurmonline.server.creatures.CreatureTemplateIds";
	}
	
	@Override
	protected boolean isValidName(String fieldName) {
		return fieldName.endsWith("_CID");
	}
	
	@Override
	protected String cleanupFieldName(String fieldName) {
		return fieldName.replaceAll("_CID$", "");
	}
	
	@Override
	protected IdType getIdFactoryType() {
		return IdType.CREATURETEMPLATE;
	}
	
	@Override
	protected int unparsable(String name) {
		throw new IllegalArgumentException(name + " is not a valid creature template id");
	}
}
