package org.gotti.wurmunlimited.mods.creaturedemo;

import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;
import org.gotti.wurmunlimited.mods.creaturedemo.creatures.Cat;
import org.gotti.wurmunlimited.mods.creaturedemo.creatures.Ocelot;
import org.gotti.wurmunlimited.mods.creaturedemo.creatures.PandaBear;
import org.gotti.wurmunlimited.mods.creaturedemo.creatures.Zebra;
import org.gotti.wurmunlimited.modsupport.creatures.ModCreatures;

public class CreatureDemo implements WurmMod, Initable {

	@Override
	public void init() {

		ModCreatures.init();

		ModCreatures.addCreature(new PandaBear());
		ModCreatures.addCreature(new Zebra());
		ModCreatures.addCreature(new Ocelot());
		ModCreatures.addCreature(new Cat());

	}

}
