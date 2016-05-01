package org.gotti.wurmunlimited.mods.creaturedemo.creatures;

import org.gotti.wurmunlimited.modsupport.CreatureTemplateBuilder;
import org.gotti.wurmunlimited.modsupport.creatures.ModCreature;
import org.gotti.wurmunlimited.modsupport.creatures.TraitsSetter;

import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.CreatureTemplate;
import com.wurmonline.server.creatures.CreatureTemplateFactory;
import com.wurmonline.server.creatures.CreatureTemplateIds;
import com.wurmonline.server.creatures.NoSuchCreatureTemplateException;

public class Cat implements ModCreature {

	@Override
	public CreatureTemplateBuilder createCreateTemplateBuilder() {
		
		return new CreatureTemplateBuilder(CreatureTemplateIds.CAT_WILD_CID) {
			@Override
			public CreatureTemplate build() {
				try {
					return CreatureTemplateFactory.getInstance().getTemplate(CreatureTemplateIds.CAT_WILD_CID);
				} catch (NoSuchCreatureTemplateException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
	
	@Override
	public boolean hasTraits() {
		return true;
	}
	
	@Override
	public String getTraitName(int trait) {
		switch (trait) {
		case 24:
			return "mainecoon";
		default:
			return null;
		}
	}
	
	@Override
	public void assignTraits(TraitsSetter traitsSetter) {
		if (Server.rand.nextInt(3) == 0) {
			traitsSetter.setTraitBit(24, true);
		}
	}
}
