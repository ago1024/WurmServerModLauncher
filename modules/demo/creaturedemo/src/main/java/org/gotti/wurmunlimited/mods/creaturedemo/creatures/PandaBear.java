package org.gotti.wurmunlimited.mods.creaturedemo.creatures;

import org.gotti.wurmunlimited.modsupport.CreatureTemplateBuilder;
import org.gotti.wurmunlimited.modsupport.creatures.EncounterBuilder;
import org.gotti.wurmunlimited.modsupport.creatures.ModCreature;
import org.gotti.wurmunlimited.modsupport.vehicles.ModVehicleBehaviour;
import org.gotti.wurmunlimited.modsupport.vehicles.VehicleFacade;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.behaviours.Vehicle;
import com.wurmonline.server.creatures.AttackAction;
import com.wurmonline.server.creatures.AttackIdentifier;
import com.wurmonline.server.creatures.AttackValues;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.shared.constants.CreatureTypes;
import com.wurmonline.server.items.Item;

public class PandaBear implements ModCreature, CreatureTypes {

	private int templateId;

	public CreatureTemplateBuilder createCreateTemplateBuilder() {

		int[] types = { C_TYPE_MOVE_GLOBAL, C_TYPE_VEHICLE, C_TYPE_ANIMAL, C_TYPE_AGG_HUMAN, C_TYPE_SWIMMING, C_TYPE_HUNTING, C_TYPE_DOMINATABLE, C_TYPE_CARNIVORE, C_TYPE_NON_NEWBIE };

		CreatureTemplateBuilder builder = new CreatureTemplateBuilder("mod.creature.panda", "Panda bear", "The panda bear has large, distinctive black patches around its eyes, over the ears, and across its round body.", "model.creature.quadraped.bear.panda", types, (byte) 2, (short) 5, (byte) 0, (short) 230, (short) 50,
				(short) 50, "sound.death.bear", "sound.death.bear", "sound.combat.hit.bear", "sound.combat.hit.bear", 0.75f, 7.0f, 0.0f, 10.0f, 0.0f, 0.0f, 1.2f, 1500, new int[] { 92, 303, 302 }, 10, 70, (byte) 72);

		this.templateId = builder.getTemplateId();

		builder.skill(102, 30.0f);
		builder.skill(104, 30.0f);
		builder.skill(103, 30.0f);
		builder.skill(100, 4.0f);
		builder.skill(101, 4.0f);
		builder.skill(105, 30.0f);
		builder.skill(106, 4.0f);
		builder.skill(10052, 40.0f);

		builder.boundsValues(-0.5f, -1.0f, 0.5f, 1.42f);
		builder.handDamString("maul");
		builder.maxAge(200);
		builder.armourType(2);
		builder.baseCombatRating(9.0f);
		builder.combatDamageType((byte) 0);
		builder.maxGroupAttackSize(4);
		builder.denName("bear cave");
		builder.denMaterial((byte) 15);
		builder.maxPercentOfCreatures(0.02f);
		builder.usesNewAttacks(true);
		builder.addPrimaryAttack(new AttackAction("maul", AttackIdentifier.STRIKE, new AttackValues(7.0f, 0.01f, 6.0f, 3, 1, (byte) 0, false, 2, 1.0f)));
		builder.addPrimaryAttack(new AttackAction("gnaw", AttackIdentifier.BITE, new AttackValues(5.0f, 0.02f, 8.0f, 3, 1, (byte) 3, false, 4, 1.1f)));
		builder.addSecondaryAttack(new AttackAction("bite", AttackIdentifier.BITE, new AttackValues(10.0f, 0.05f, 6.0f, 2, 1, (byte) 3, false, 3, 1.1f)));
		builder.addSecondaryAttack(new AttackAction("scratch", AttackIdentifier.STRIKE, new AttackValues(7.0f, 0.05f, 6.0f, 2, 1, (byte) 1, false, 8, 1.0f)));

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
				vehicle.setSeatFightMod(0, 0.8f, 1.1f);
				vehicle.setSeatOffset(0, 0.0f, 0.0f, 0.0f);
				vehicle.setCreature(true);
				vehicle.setSkillNeeded(23.0f);
				vehicle.setName(creature.getName());
				vehicle.setMaxHeightDiff(0.04f);
				vehicle.setMaxDepth(-0.7f);
				vehicle.setMaxSpeed(20.0f);
				vehicle.setCommandType((byte) 3);
			}
		};
	}
	
	@Override
	public void addEncounters() {
		if (templateId == 0)
			return;
		
		new EncounterBuilder(Tiles.Tile.TILE_TREE.id)
			.addCreatures(templateId, 2)
			.build(1);
		
		new EncounterBuilder(Tiles.Tile.TILE_CAVE.id, (byte) -1)
			.addCreatures(templateId, 2)
			.build(4);
	}

}
