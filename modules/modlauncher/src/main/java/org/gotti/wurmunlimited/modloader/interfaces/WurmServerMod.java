package org.gotti.wurmunlimited.modloader.interfaces;

public interface WurmServerMod extends Versioned {
	
	public default void init() {
	}
	
	public default void preInit() {
	}

}
