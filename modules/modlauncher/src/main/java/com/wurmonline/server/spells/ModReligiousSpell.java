package com.wurmonline.server.spells;

import com.wurmonline.mesh.Tiles.TileBorderDirection;
import com.wurmonline.server.bodys.Wound;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.skills.Skill;

public class ModReligiousSpell extends ReligiousSpell {

	public ModReligiousSpell(String aName, int aNum, int aCastingTime, int aCost, int aDifficulty, int aLevel, long cooldown) {
		super(aName, aNum, aCastingTime, aCost, aDifficulty, aLevel, cooldown);
	}

	@Override
	public void doEffect(Skill castSkill, double power, Creature performer, Creature target) {
		super.doEffect(castSkill, power, performer, target);
	}

	@Override
	public void doNegativeEffect(Skill castSkill, double power, Creature performer, Creature target) {
		super.doNegativeEffect(castSkill, power, performer, target);
	}

	@Override
	public void doEffect(Skill castSkill, double power, Creature performer, Item target) {
		super.doEffect(castSkill, power, performer, target);
	}

	@Override
	public void doNegativeEffect(Skill castSkill, double power, Creature performer, Item target) {
		super.doNegativeEffect(castSkill, power, performer, target);
	}

	@Override
	public void doEffect(Skill castSkill, double power, Creature performer, Wound target) {
		super.doEffect(castSkill, power, performer, target);
	}

	@Override
	public void doEffect(Skill castSkill, double power, Creature performer, int tilex, int tiley, int layer, int heightOffset, TileBorderDirection dir) {
		super.doEffect(castSkill, power, performer, tilex, tiley, layer, heightOffset, dir);
	}

	@Override
	public void doEffect(Skill castSkill, double power, Creature performer, int tilex, int tiley, int layer, int heightOffset) {
		super.doEffect(castSkill, power, performer, tilex, tiley, layer, heightOffset);
	}

	@Override
	public boolean precondition(Skill castSkill, Creature performer, Creature target) {
		return super.precondition(castSkill, performer, target);
	}

	@Override
	public boolean precondition(Skill castSkill, Creature performer, Item target) {
		return super.precondition(castSkill, performer, target);
	}

	@Override
	public boolean postcondition(Skill castSkill, Creature performer, Item target, double effect) {
		return super.postcondition(castSkill, performer, target, effect);
	}

	@Override
	public boolean precondition(Skill castSkill, Creature performer, Wound target) {
		return super.precondition(castSkill, performer, target);
	}

	@Override
	public boolean precondition(Skill castSkill, Creature performer, int tilex, int tiley, int layer) {
		return super.precondition(castSkill, performer, tilex, tiley, layer);
	}

	@Override
	public boolean precondition(Skill castSkill, Creature performer, int tilex, int tiley, int layer, int heightOffset, TileBorderDirection dir) {
		return super.precondition(castSkill, performer, tilex, tiley, layer, heightOffset, dir);
	}

}
