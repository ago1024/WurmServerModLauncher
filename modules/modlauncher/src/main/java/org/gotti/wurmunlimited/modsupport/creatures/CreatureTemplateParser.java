package org.gotti.wurmunlimited.modsupport.creatures;

import org.gotti.wurmunlimited.modsupport.NamedIdParser;

import com.wurmonline.server.creatures.CreatureTemplateIds;

/**
 * Parse a list of creature templates and creature template ids.
 */
public class CreatureTemplateParser extends NamedIdParser {
	
	@Override
	protected Class<?> getNamesClass() {
		return CreatureTemplateIds.class;
	}
	
	@Override
	protected boolean isValidName(String fieldName) {
		return fieldName.endsWith("_cid");
	}
	
	@Override
	protected String cleanupFieldName(String fieldName) {
		return fieldName.replaceAll("_cid$", "");
	}
	
	@Override
	protected int unparsable(String name) {
		throw new IllegalArgumentException(name + "is not a valid CreatureTemplateId");
	}
}
