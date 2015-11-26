package org.gotti.wurmunlimited.modsupport.creatures;

import org.gotti.wurmunlimited.modsupport.CreatureTemplateBuilder;
import org.gotti.wurmunlimited.modsupport.vehicles.ModVehicleBehaviour;

public interface ModCreature {

	CreatureTemplateBuilder createCreateTemplateBuilder();
	
	default ModVehicleBehaviour getVehicleBehaviour() {
		return null;
	}
	
	default public void addEncounters() {
	}
}
