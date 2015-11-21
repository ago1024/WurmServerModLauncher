package org.gotti.wurmunlimited.mods.hitchingpost;

import org.gotti.wurmunlimited.modsupport.vehicles.ModVehicleBehaviour;
import org.gotti.wurmunlimited.modsupport.vehicles.VehicleFacade;

import com.wurmonline.server.behaviours.Seat;
import com.wurmonline.server.behaviours.Vehicle;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;

public class HitchingPostBehaviour extends ModVehicleBehaviour {

	public void setSettingsForVehicle(final Creature creature, final Vehicle vehicle) {
	}

	public void setSettingsForVehicle(final Item item, final Vehicle v) {
		VehicleFacade vehicle = wrap(v);
		vehicle.setUnmountable(true);
		vehicle.createOnlyPassengerSeats(1);
		vehicle.setSeatFightMod(0, 0.7f, 0.4f);
		vehicle.setCreature(false);
		vehicle.setEmbarkString("enter");
		vehicle.setName(item.getName());
		vehicle.setMaxDepth(-0.7f);
		vehicle.setMaxHeightDiff(0.04f);
		vehicle.setCommandType((byte)2);
		
		
		final Seat[] hitches = { createSeat(Seat.TYPE_HITCHED), createSeat(Seat.TYPE_HITCHED), createSeat(Seat.TYPE_HITCHED) };
		hitches[0].offx = 2.0f;
		hitches[0].offy = 1.0f;
		hitches[1].offx = 2.0f;
		hitches[1].offy = 0.0f;
		hitches[2].offx = 2.0f;
		hitches[2].offy = -1.0f;
		vehicle.addHitchSeats(hitches);
	}
}
