package org.gotti.wurmunlimited.modsupport.creatures;

import org.assertj.core.api.Assertions;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javassist.CtClass;
import javassist.CtField;

public class CreatureTypesParserTest {
	
	private CreatureTypesParser parser;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void setup() {
		parser = new CreatureTypesParser();
	}

	/**
	 * Simple test case. Parse a name
	 */
	@Test
	public void test() {
		Assertions.assertThat(parser.parse("herd")).isEqualTo(10);
	}
	
	/**
	 * Simple test case. Parse an id
	 */
	@Test
	public void testId() {
		Assertions.assertThat(parser.parse("10")).isEqualTo(10);
	}
	
	/**
	 * Simple test case. Parse an invalid value
	 */
	@Test
	public void testInvalid() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("test is not a valid creature type");
		Assertions.assertThat(parser.parse("test")).isEqualTo(37);
	}
	
	/**
	 * Test adding a new id after querying an id
	 */
	@Test
	public void testDefrosting() throws Exception {
		Assertions.assertThat(parser.parse("herd")).isEqualTo(10);
		
		// Add a new field
		CtClass ctIds = HookManager.getInstance().getClassPool().get("com.wurmonline.shared.constants.CreatureTypes");
		ctIds.addField(CtField.make("public static final int C_TYPE_TEST = 37;", ctIds));
		
		// Test that it works
		Assertions.assertThat(new CreatureTypesParser().parse("test")).isEqualTo(37);
		
		// test that same does not work on the old parser
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("test is not a valid creature type");
		Assertions.assertThat(parser.parse("test")).isEqualTo(37);
	}
}
