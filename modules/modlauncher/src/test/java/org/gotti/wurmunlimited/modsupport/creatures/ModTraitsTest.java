package org.gotti.wurmunlimited.modsupport.creatures;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.BitSet;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.IntStream;

import org.gotti.wurmunlimited.modsupport.creatures.ModTraits.TraitsInfo;
import org.junit.Test;

import com.wurmonline.server.MiscConstants;

public class ModTraitsTest {

	@Test
	public void testPickOneTrait() {
		Random random = new Random(1);
		Map<Integer, Integer> traitMap = new TreeMap<>();
		traitMap.put(1, 20);
		traitMap.put(2, 80);
		traitMap.put(3, 20);
		assertThat(ModTraits.pickOneTrait(random, traitMap)).isEqualTo(3);
		assertThat(ModTraits.pickOneTrait(random, traitMap)).isEqualTo(2);
		assertThat(ModTraits.pickOneTrait(random, traitMap)).isEqualTo(1);
	}
	
	@Test
	public void testCalcNewTrais() {
		Random random = new Random(1);
		
		long mothertraits = 1 << 28;
		long fathertraits = 1 << 28;
		long regulartraits = ModTraits.REGULAR_TRAITS;
		long colortraits = ModTraits.COLOR_TRAITS | 1 << 28;

		TraitsInfo traitsInfo = new TraitsInfo() {
			@Override
			public boolean isTraitNegative(int trait) {
				return ModTraitsTest.isTraitNegative(trait);
			}
			@Override
			public boolean isTraitNeutral(int trait) {
				return ModTraitsTest.isTraitNeutral(trait);
			}
		};
		
		long traits = ModTraits.calcNewTraits(random, 90, false, mothertraits, fathertraits, regulartraits, colortraits, false, traitsInfo);
		
		BitSet expected = new BitSet();
		IntStream.of(7, 28, 49, 54, 59, 63).forEach(expected::set);
		
		BitSet traitBits = new BitSet();
		ModTraits.setTraitBits(traits, traitBits);
		
		assertThat(traitBits).isEqualTo(expected);
	}

	/**
	 * Reimplement Traits.isTraitNegative(int) to allow testing with the server stub artifacts
	 * @param trait trait number
	 * @return true if the trait is negative
	 */
	private static boolean isTraitNegative(int trait) {
		switch (trait) {
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 19:
				return true;
			default:
				return false;
		}
	}

	/**
	 * Reimplement Traits.isTraitNeutral(int) to allow testing with the server stub artifacts
	 * @param trait trait number
	 * @return true if the trait is neutral
	 */
	private static boolean isTraitNeutral(int trait) {
		switch (trait) {
			case 22:
			case 27:
			case 28:
			case 63:
			case 29:
			case 15:
			case 16:
			case 17:
			case 18:
			case 24:
			case 25:
			case 23:
			case 30:
			case 31:
			case 32:
			case 33:
			case 34:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * verify that the server code did not change the number of traits
	 */
	@Test
	public void testMaxColorTraits() {
		assertThat(MiscConstants.CURRENT_MAX_TRAIT).isEqualTo(34);
	}
	
	/**
	 * verify that the server code did not change the number of creature colours
	 */
	@Test
	public void testNumCreatureColours() {
		assertThat(MiscConstants.CURRENT_NUM_CREATURE_COLOURS).isEqualTo(13);
	}
}
