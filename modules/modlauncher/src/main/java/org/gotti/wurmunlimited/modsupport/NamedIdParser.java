package org.gotti.wurmunlimited.modsupport;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Parse a list of names or ids
 */
public abstract class NamedIdParser {

	private static final Logger logger = Logger.getLogger(NamedIdParser.class.getName());

	private Map<String, Integer> nameToId = new HashMap<>();
	private Map<Integer, String> idToName = new HashMap<>();

	/**
	 * Initialize the known creature type names
	 */
	public NamedIdParser() {
		final Class<?> namesClass = getNamesClass();
		for (Field field : namesClass.getFields()) {
			final String fieldName = field.getName();
			if (isValidName(fieldName)) {
				final String name = cleanupFieldName(fieldName);
				final String normalized = normalizeName(name);
				try {
					int id = field.getInt(namesClass);
					nameToId.put(normalized, id);
					idToName.put(id, name);
				} catch (IllegalAccessException e) {
					logger.log(Level.WARNING, null, e);
				}
			}
		}
	}

	protected abstract Class<?> getNamesClass();

	/**
	 * Types prefixed with "mod:" will be looked up in the IdFactory using this type
	 * @return type of null
	 */
	protected IdType getIdFactoryType() {
		return null;
	}

	/**
	 * Test if the field name is a valid entity name.
	 */
	protected boolean isValidName(String fieldName) {
		return true;
	}

	/**
	 * Cleanup the field name
	 */
	protected String cleanupFieldName(String fieldName) {
		return fieldName;
	}

	/**
	 * Normalize the entity name into a lookup key for the hashmap.
	 * Default is to lower case and to remove all underscores and spaces
	 */
	public static String normalizeName(String name) {
		return name.toLowerCase().replaceAll("_| ", "");
	}

	/**
	 * Name is unparsable. Fail with an exception or replace with a default value
	 */
	protected int unparsable(String name) {
		throw new IllegalArgumentException(name);
	}

	/**
	 * Parse the entity name or id
	 */
	public int parse(String name) {
		if (getIdFactoryType() != null && name.startsWith("mod:")) {
			int id = IdFactory.getExistingIdFor(name.substring(4), getIdFactoryType());
			if (id != -10)
				return id;
			return unparsable(name);
		}
		try {
			return Integer.parseInt(name);
		} catch (NumberFormatException e) {
		}
		Integer id = nameToId.get(normalizeName(name));
		if (id != null) {
			return id;
		}
		return unparsable(name);
	}

	/**
	 * Parse a comma separated list of entity names or ids
	 */
	public int[] parseList(String str) {
		return Arrays.stream(str.split(","))
				.map(String::trim)
				.mapToInt(this::parse)
				.toArray();
	}

	/**
	 * Convert an id into a proper entity name
	 */
	public String toString(int id) {
		String name = idToName.get(id);
		if (name != null) {
			return name;
		} else {
			return Integer.toString(id);
		}
	}

	/**
	 * Convert an id array into a comma separated list of entity names
	 */
	public String toString(int[] ids) {
		return Arrays.stream(ids)
				.mapToObj(this::toString)
				.collect(Collectors.joining(", "));
	}
}
