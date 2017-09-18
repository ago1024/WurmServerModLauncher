package org.gotti.wurmunlimited.mods.bagofholding;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modsupport.actions.ActionEntryBuilder;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemSpellEffects;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.spells.ModReligiousSpell;
import com.wurmonline.server.spells.Spell;
import com.wurmonline.server.spells.SpellEffect;

public class BagOfHolding extends ModReligiousSpell {

	private static Logger logger = Logger.getLogger(BagOfHolding.class.getName());

	public BagOfHolding(int cost, int difficulty, long cooldown) {

		super("Bag of Holding", ModActions.getNextActionId(), 30, cost, difficulty, 30, cooldown);
		this.targetItem = true;

		try {
			ReflectionUtil.setPrivateField(this, ReflectionUtil.getField(Spell.class, "description"), "Increases the volume of a container");
		} catch (Exception e) {
			logger.log(Level.WARNING, null, e);
		}

		// Copied from Courier
		ActionEntry actionEntry = new ActionEntryBuilder((short) number, name, "casting", new int[] { 2 /* ACTION_TYPE_SPELL */, 36 /* ACTION_TYPE_ALWAYS_USE_ACTIVE_ITEM */, 48 /* ACTION_TYPE_ENEMY_ALWAYS */}).build();
		ModActions.registerAction(actionEntry);
	}

	public static boolean isValidTarget(Item target) {
		return target.isHollow() && !target.isMailBox() && !target.isSpringFilled();
	}

	@Override
	public boolean precondition(final Skill castSkill, final Creature performer, final Item target) {
		if (!isValidTarget(target)) {
			performer.getCommunicator().sendNormalServerMessage("The spell will not work on that.");
			return false;
		}
		return true;
	}

	@Override
	public boolean precondition(final Skill castSkill, final Creature performer, final Creature target) {
		return false;
	}

	@Override
	public void doEffect(final Skill castSkill, final double power, final Creature performer, final Item target) {
		if (!isValidTarget(target)) {
			performer.getCommunicator().sendNormalServerMessage("The spell fizzles.");
			return;
		}

		ItemSpellEffects effs = target.getSpellEffects();
		if (effs == null) {
			effs = new ItemSpellEffects(target.getWurmId());
		}
		SpellEffect eff = effs.getSpellEffect(BUFF_COURIER);
		if (eff == null) {
			performer.getCommunicator().sendNormalServerMessage("You magicly enlarge the " + target.getName() + ".");
			eff = new SpellEffect(target.getWurmId(), BUFF_COURIER, (float) power, 20000000);
			effs.addSpellEffect(eff);
			Server.getInstance().broadCastAction(String.valueOf(performer.getName()) + " looks pleased as " + performer.getHeSheItString() + " magicly enlarges the " + target.getName() + ".", performer, 5);
		} else if (eff.getPower() > power) {
			performer.getCommunicator().sendNormalServerMessage("You frown as you fail to enlarge the " + target.getName() + ".");
			Server.getInstance().broadCastAction(String.valueOf(performer.getName()) + " frowns.", performer, 5);
		} else {
			performer.getCommunicator().sendNormalServerMessage("You succeed in enlarging the " + target.getName() + ".");
			eff.improvePower((float) power);
			Server.getInstance().broadCastAction(String.valueOf(performer.getName()) + " looks pleased as " + performer.getHeSheItString() + " enlarges the " + target.getName() + ".", performer, 5);
		}
	}

	public static float getSpellEffect(Item target) {
		if (!isValidTarget(target))
			return 0;

		ItemSpellEffects effs = target.getSpellEffects();
		if (effs == null)
			return 0;

		SpellEffect eff = effs.getSpellEffect(BUFF_COURIER);
		if (eff == null)
			return 0;
		
		return eff.getPower();
	}
}
