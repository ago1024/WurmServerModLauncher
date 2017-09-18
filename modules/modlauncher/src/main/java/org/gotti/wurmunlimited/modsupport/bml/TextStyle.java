package org.gotti.wurmunlimited.modsupport.bml;

import java.util.Locale;

public enum TextStyle {

	BOLD,
	ITALIC,
	BOLDITALIC,
	;

	public String getType() {
		return name().toLowerCase(Locale.ROOT);
	}
}
