package org.gotti.wurmunlimited.modsupport.creatures;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.BitSet;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.junit.Test;

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
		expected.set(4);
		expected.set(10);
		expected.set(13);
		expected.set(16);
		expected.set(20);
		expected.set(28);
		expected.set(63);
		
		BitSet traitBits = new BitSet();
		ModTraits.setTraitBits(traits, traitBits);
		
		assertThat(traitBits).isEqualTo(expected);
	}
	

}
