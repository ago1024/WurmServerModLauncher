package org.gotti.wurmunlimited.mods.creaturedemo.creatures;

import org.gotti.wurmunlimited.modsupport.CreatureTemplateBuilder;
import org.gotti.wurmunlimited.modsupport.creatures.EncounterBuilder;
import org.gotti.wurmunlimited.modsupport.creatures.ModCreature;
import org.gotti.wurmunlimited.modsupport.vehicles.ModVehicleBehaviour;
import org.gotti.wurmunlimited.modsupport.vehicles.VehicleFacade;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.behaviours.Vehicle;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.CreatureTypes;
import com.wurmonline.server.items.Item;

public class Zebra implements ModCreature, CreatureTypes {

	private int templateId;

	public CreatureTemplateBuilder createCreateTemplateBuilder() {

		final int[] types = { C_TYPE_MOVE_LOCAL, C_TYPE_SWIMMING, C_TYPE_VEHICLE, C_TYPE_DOMESTIC, C_TYPE_ANIMAL, C_TYPE_LEADABLE, C_TYPE_GRAZER, C_TYPE_HERBIVORE, C_TYPE_DOMINATABLE };

		CreatureTemplateBuilder builder = new CreatureTemplateBuilder("mod.creature.zebra", "Zebra", "Zebras like this one have many stripes.", "model.creature.quadraped.horse.zebra", types, (byte) 1, (short) 3, (byte) 0, (short) 180, (short) 50, (short) 250, "sound.death.horse",
				"sound.death.horse", "sound.combat.hit.horse", "sound.combat.hit.horse", 1.0f, 1.0f, 2.5f, 1.5f, 2.0f, 0.0f, 1.5f, 100, new int[] { 307, 306, 140, 71, 309, 308 }, 5, 0, (byte) 79);

		this.templateId = builder.getTemplateId();

		builder.skill(102, 25.0f);
		builder.skill(104, 20.0f);
		builder.skill(103, 40.0f);
		builder.skill(100, 7.0f);
		builder.skill(101, 7.0f);
		builder.skill(105, 22.0f);
		builder.skill(106, 5.0f);
		builder.skill(10052, 28.0f);

		builder.maxAge(200);
		builder.baseCombatRating(6.0f);
		builder.combatDamageType((byte) 0);
		builder.alignment(100.0f);
		builder.handDamString("kick");
		builder.armourType(6);
		builder.isHorse(true);
		builder.maxPercentOfCreatures(0.02f);

		return builder;
	}

	public ModVehicleBehaviour getVehicleBehaviour() {

		return new ModVehicleBehaviour() {

			@Override
			public void setSettingsForVehicle(Item item, Vehicle vehicle) {
			}

			@Override
			public void setSettingsForVehicle(Creature creature, Vehicle v) {
				VehicleFacade vehicle = wrap(v);

				vehicle.createPassengerSeats(0);
				vehicle.setSeatFightMod(0, 0.7f, 0.9f);
				vehicle.setSeatOffset(0, 0.0f, 0.0f, 0.0f);
				vehicle.setCreature(true);
				vehicle.setSkillNeeded(21.0f);
				vehicle.setName(creature.getName());
				vehicle.setMaxDepth(-0.7f);
				vehicle.setMaxHeightDiff(0.04f);
				vehicle.setMaxSpeed(30.0f);
				vehicle.setCommandType((byte) 3);
				vehicle.setCanHaveEquipment(true);
			}
		};
	}
	
	@Override
	public void addEncounters() {
		if (templateId == 0)
			return;
		
		new EncounterBuilder(Tiles.Tile.TILE_STEPPE.id)
			.addCreatures(templateId, 2)
			.build(2);
	}
}
