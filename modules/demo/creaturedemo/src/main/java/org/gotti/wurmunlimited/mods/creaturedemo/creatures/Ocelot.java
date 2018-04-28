package org.gotti.wurmunlimited.mods.creaturedemo.creatures;

import org.gotti.wurmunlimited.modsupport.CreatureTemplateBuilder;
import org.gotti.wurmunlimited.modsupport.creatures.EncounterBuilder;
import org.gotti.wurmunlimited.modsupport.creatures.ModCreature;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.shared.constants.CreatureTypes;

public class Ocelot implements ModCreature, CreatureTypes {

	private static final String MOD_CREATURE_OCELOT = "mod.creature.ocelot";
	private int templateId;

	public CreatureTemplateBuilder createCreateTemplateBuilder() {
		
		final int[] types = { C_TYPE_MOVE_LOCAL, C_TYPE_ANIMAL, C_TYPE_AGG_HUMAN, C_TYPE_HUNTING, C_TYPE_CLIMBER, C_TYPE_DOMINATABLE, C_TYPE_CARNIVORE };

		CreatureTemplateBuilder builder = new CreatureTemplateBuilder(MOD_CREATURE_OCELOT, "Ocelot", "Looking like a huge cat, with a dappled coat.", "model.creature.quadraped.lion.ocelot", types, (byte) 3, (short) 5, (byte) 0, (short) 60, (short) 30, (short) 90, "sound.death.lion",
				"sound.death.lion", "sound.combat.hit.lion", "sound.combat.hit.lion", 0.95f, 3.0f, 0.0f, 5.0f, 0.0f, 0.0f, 1.0f, 1200, new int[] { 92, 305, 313 }, 10, 40, (byte) 75);
		
		this.templateId = builder.getTemplateId();

		builder.skill(102, 15.0f);
		builder.skill(104, 3.0f);
		builder.skill(103, 15.0f);
		builder.skill(100, 7.0f);
		builder.skill(101, 8.0f);
		builder.skill(105, 25.0f);
		builder.skill(106, 4.0f);
		builder.skill(10052, 6.0f);

		builder.handDamString("claw");
		builder.kickDamString("claw");
		builder.maxAge(100);
		builder.armourType(6);
		builder.baseCombatRating(3.0f);
		builder.combatDamageType((byte) 1);
		builder.maxGroupAttackSize(2);
		builder.denName("mountain lion hideout");
		builder.denMaterial((byte) 15);
		builder.maxPercentOfCreatures(0.06f);

		return builder;
	}
	
	public void addEncounters() {
		if (templateId == 0)
			return;
		
		new EncounterBuilder(Tiles.Tile.TILE_TREE.id)
			.addCreatures(templateId, 2)
			.build(1);
		
		new EncounterBuilder(Tiles.Tile.TILE_CAVE.id, (byte) -1)
			.addCreatures(templateId, 2)
			.build(1);
	}
}
