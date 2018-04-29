package org.gotti.wurmunlimited.modsupport.creatures;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.HookException;

import com.wurmonline.server.Server;
import com.wurmonline.server.Servers;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.Traits;

public class ModTraits {
	
	public static final int TRAIT_FIERCLY = 0;
	public static final int TRAIT_FLEETER = 1;
	public static final int TRAIT_TOUGH_BUGGER = 2;
	public static final int TRAIT_STRONG_BODY = 3;
	public static final int TRAIT_LIGHTNING = 4;
	public static final int TRAIT_CARRY_MORE = 5;
	public static final int TRAIT_STRONG_LEGS = 6;
	public static final int TRAIT_KEEN_SENSES = 7;
	public static final int TRAIT_MALFORMED_LEGS = 8;
	public static final int TRAIT_DIFFERNENT_LENGTH = 9;
	public static final int TRAIT_OVERLY_AGGRESSIVE = 10;
	public static final int TRAIT_UNMOTIVATED = 11;
	public static final int TRAIT_STRONG_WILLED = 12;
	public static final int TRAIT_ILLNESS = 13;
	public static final int TRAIT_CONSTANTLY_HUNGRY = 14;
	public static final int TRAIT_FEEBLE_AND_UNHEALTHY = 19;
	public static final int TRAIT_STRONG_AND_HEALTHY = 20;
	public static final int TRAIT_SPARK = 21;
	public static final int TRAIT_CORRUPTED = 22;
	public static final int TRAIT_RIFT = 27;
	public static final int TRAIT_TRAITOR = 28;
	public static final int TRAIT_VALREI = 29;
	
	public static final int COLOR_EBONY_BLACK = 23;
	public static final int COLOR_BLOOD_BAY = 25;
	public static final int COLOR_PIEBALD_PINTO = 24;
	public static final int COLOR_WHITE = 18;
	public static final int COLOR_BLACK = 17;
	public static final int COLOR_GOLD = 16;
	public static final int COLOR_BROWN = 15;
	
	public static final int REGULAR_TRAITS = 
			1 << TRAIT_FIERCLY |
			1 << TRAIT_FLEETER |
			1 << TRAIT_TOUGH_BUGGER |
			1 << TRAIT_STRONG_BODY |
			1 << TRAIT_LIGHTNING |
			1 << TRAIT_CARRY_MORE |
			1 << TRAIT_STRONG_LEGS |
			1 << TRAIT_KEEN_SENSES |
			1 << TRAIT_MALFORMED_LEGS |
			1 << TRAIT_DIFFERNENT_LENGTH |
			1 << TRAIT_OVERLY_AGGRESSIVE |
			1 << TRAIT_UNMOTIVATED |
			1 << TRAIT_STRONG_WILLED |
			1 << TRAIT_ILLNESS |
			1 << TRAIT_CONSTANTLY_HUNGRY |
			1 << TRAIT_FEEBLE_AND_UNHEALTHY |
			1 << TRAIT_STRONG_AND_HEALTHY |
			1 << TRAIT_SPARK;
	
	public static final int COLOR_TRAITS = 
			1 << COLOR_EBONY_BLACK |
			1 << COLOR_BLOOD_BAY |
			1 << COLOR_PIEBALD_PINTO |
			1 << COLOR_WHITE |
			1 << COLOR_BLACK |
			1 << COLOR_GOLD |
			1 << COLOR_BROWN;

	private static final Logger LOGGER = Logger.getLogger(Traits.class.getName());

	private static Method creatureGetTraits;
	static {
		try {
			creatureGetTraits = ReflectionUtil.getMethod(Creature.class, "getTraits");
		} catch (NoSuchMethodException e) {
			throw new HookException(e);
		}
	}

	public static long getTraits(Creature creature) {
		try {
			return ReflectionUtil.callPrivateMethod(creature, creatureGetTraits, new Object[] {});
		} catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
			throw new HookException(e);
		}
	}

	public static long calcNewTraits(final double breederSkill, final boolean inbred, final long mothertraits, final long fathertraits, final long regulartraits, final long colortraits) {
		final Random rand = new Random();
		return calcNewTraits(rand, breederSkill, inbred, mothertraits, fathertraits, regulartraits, colortraits, Servers.isThisAPvpServer());
	}
	
	public static long calcNewTraits(Random rand, final double breederSkill, final boolean inbred, final long mothertraits, final long fathertraits, final long regulartraits, final long colortraits, boolean isThisAPvpServer) {
		
		final BitSet motherSet = new BitSet(64);
		final BitSet fatherSet = new BitSet(64);
		final BitSet childSet = new BitSet(64);
		final BitSet availableSet = new BitSet(64);
		
		final int maxTraits = Math.min(8, Math.max(1, (int) (breederSkill / 10.0)));
		final int maxPoints = maxTraits * 60;
		
		int allocated = 0;
		final Map<Integer, Integer> newSet = new HashMap<Integer, Integer>();
		final List<Integer> availableTraits = new ArrayList<Integer>();
		
		setTraitBits(fathertraits, fatherSet);
		setTraitBits(mothertraits, motherSet);
		setTraitBits(regulartraits | colortraits, availableSet);
		
		for (int bitIndex = 0; bitIndex < 64; ++bitIndex) {
			if (!availableSet.get(bitIndex))
				continue;
			
			availableTraits.add(bitIndex);
			if (motherSet.get(bitIndex) && fatherSet.get(bitIndex)) {
				int num = 50;
				if (inbred && Traits.isTraitNegative(bitIndex)) {
					num += 10;
				}
				newSet.put(bitIndex, num);
				if (!Traits.isTraitNeutral(bitIndex)) {
					allocated += 50;
				}
				availableTraits.remove((Object) bitIndex);
			} else if (motherSet.get(bitIndex)) {
				int num = 30;
				if (inbred && Traits.isTraitNegative(bitIndex)) {
					num += 10;
				}
				newSet.put(bitIndex, num);
				if (!Traits.isTraitNeutral(bitIndex)) {
					allocated += 30;
				}
				availableTraits.remove((Object) bitIndex);
			} else if (fatherSet.get(bitIndex)) {
				int num = 20;
				if (inbred && Traits.isTraitNegative(bitIndex)) {
					num += 10;
				}
				newSet.put(bitIndex, num);
				if (!Traits.isTraitNeutral(bitIndex)) {
					allocated += 20;
				}
				availableTraits.remove((Object) bitIndex);
			}
		}
		
		final int left = maxPoints - allocated;
		float traitsLeft = 0.0f;
		if (left > 0) {
			traitsLeft = left / 50.0f;
			if (traitsLeft - (int) traitsLeft > 0.0f) {
				++traitsLeft;
			}
			for (int x = 0; x < (int) traitsLeft; ++x) {
				if (rand.nextBoolean()) {
					int num2 = 20;
					final Integer newTrait = availableTraits.remove(rand.nextInt(availableTraits.size()));
					if (Traits.isTraitNegative(newTrait)) {
						num2 -= maxTraits;
						if (inbred) {
							num2 += 10;
						}
					}
					if (Traits.isTraitNeutral(newTrait)) {
						--x;
					}
					newSet.put(newTrait, num2);
				}
			}
			traitsLeft = maxTraits;
		} else {
			traitsLeft = Math.max(Math.min(newSet.size(), maxTraits), 3 + Server.rand.nextInt(3));
		}
		for (int t = 0; t < traitsLeft && !newSet.isEmpty(); ++t) {
			final Integer selected = pickOneTrait(rand, newSet);
			if (selected >= 0) {
				if (selected != 22 && selected != 27) {
					childSet.set(selected, true);
					newSet.remove(selected);
					if (Traits.isTraitNeutral(selected)) {
						--t;
					}
				}
			} else {
				LOGGER.log(Level.WARNING, "Failed to select a trait from a map of size " + newSet.size());
			}
		}
		if (!isThisAPvpServer) {
			childSet.clear(22);
		} else if (fatherSet.get(22) || motherSet.get(22)) {
			childSet.set(22);
		}
		childSet.set(63, true);
		return getTraitBits(childSet);
	}

	static Integer pickOneTrait(final Random rand, final Map<Integer, Integer> traitMap) {
		int chance = 0;
		for (final Map.Entry<Integer, Integer> entry : traitMap.entrySet()) {
			chance += entry.getValue();
		}
		if (chance == 0 || chance < 0) {
			LOGGER.log(Level.INFO, "Trait rand=" + chance + " should not be <=0! Size of map is " + traitMap.size());
			return -1;
		}
		final int selectedTrait = rand.nextInt(chance);
		chance = 0;
		for (final Map.Entry<Integer, Integer> entry2 : traitMap.entrySet()) {
			chance += entry2.getValue();
			if (chance > selectedTrait) {
				return entry2.getKey();
			}
		}
		return -1;
	}

	static BitSet setTraitBits(final long bits, final BitSet toSet) {
		for (int x = 0; x < 64; ++x) {
			if (x == 0) {
				if ((bits & 0x1L) == 0x1L) {
					toSet.set(x, true);
				} else {
					toSet.set(x, false);
				}
			} else if ((bits >> x & 0x1L) == 0x1L) {
				toSet.set(x, true);
			} else {
				toSet.set(x, false);
			}
		}
		return toSet;
	}

	static long getTraitBits(final BitSet bitsprovided) {
		return bitsprovided.toLongArray()[0];
	}

}
