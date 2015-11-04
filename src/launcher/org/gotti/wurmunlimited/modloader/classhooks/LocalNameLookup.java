package org.gotti.wurmunlimited.modloader.classhooks;

import javassist.NotFoundException;
import javassist.bytecode.LocalVariableAttribute;

public class LocalNameLookup {

	private LocalVariableAttribute attr;

	public LocalNameLookup(LocalVariableAttribute attribute) {
		this.attr = attribute;
	}

	public int get(String name) throws NotFoundException {
		if (name == null) {
			throw new NotFoundException(name);
		}
		for (int i = 0; i < attr.tableLength(); i++) {
			if (name.equals(attr.variableName(i))) {
				return attr.index(i);
			}
		}
		throw new NotFoundException(name);
	}

}
