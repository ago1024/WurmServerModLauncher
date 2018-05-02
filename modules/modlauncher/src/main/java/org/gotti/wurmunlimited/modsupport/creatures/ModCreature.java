package org.gotti.wurmunlimited.modsupport.creatures;

import org.gotti.wurmunlimited.modsupport.CreatureTemplateBuilder;
import org.gotti.wurmunlimited.modsupport.vehicles.ModVehicleBehaviour;

import com.wurmonline.server.creatures.Traits;

public interface ModCreature {

	CreatureTemplateBuilder createCreateTemplateBuilder();
	
	default ModVehicleBehaviour getVehicleBehaviour() {
		return null;
	}
	
	default public void addEncounters() {
	}
	
	default String getTraitName(int trait) {
		return null;
	}
	
	default String getColourName(int trait) {
		return null;
	}
	
	default void assignTraits(TraitsSetter traitsSetter) {
	}

	default boolean hasTraits() {
		return false;
	}

	default long calcNewTraits(double breederSkill, boolean inbred, long mothertraits, long fathertraits) {
		return Traits.calcNewTraits(breederSkill, inbred, mothertraits, fathertraits);
	}
}
