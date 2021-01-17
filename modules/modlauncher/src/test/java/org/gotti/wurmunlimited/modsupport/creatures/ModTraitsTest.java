package org.gotti.wurmunlimited.modsupport.creatures;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.BitSet;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.IntStream;

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
		
		long traits = ModTraits.calcNewTraits(random, 90, false, mothertraits, fathertraits, regulartraits, colortraits, false);
		
		BitSet expected = new BitSet();
		IntStream.of(7, 28, 49, 54, 59, 63).forEach(expected::set);
		
		BitSet traitBits = new BitSet();
		ModTraits.setTraitBits(traits, traitBits);
		
		assertThat(traitBits).isEqualTo(expected);
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
