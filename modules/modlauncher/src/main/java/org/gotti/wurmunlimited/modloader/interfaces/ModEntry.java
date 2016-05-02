package org.gotti.wurmunlimited.modloader.interfaces;

import java.util.Properties;

public interface ModEntry {
	
	String getName();
	
	Properties getProperties();
	
	WurmServerMod getWurmMod();

}
