package org.gotti.wurmunlimited.modloader;

import org.gotti.wurmunlimited.modcomm.ModComm;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;

public class ModLoader extends ModLoaderShared<WurmServerMod> {
	
	public ModLoader() {
		super(WurmServerMod.class);
	}
	
	@Override
	protected void modcommInit() {
		ModComm.init();
	}
	
	@Override
	protected void preInit() {
	}
	
	@Override
	protected void init() {
	};
}
