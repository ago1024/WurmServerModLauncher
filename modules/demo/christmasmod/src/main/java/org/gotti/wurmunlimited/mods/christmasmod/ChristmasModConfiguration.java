package org.gotti.wurmunlimited.mods.christmasmod;

import org.gotti.wurmunlimited.modsupport.items.ItemIdParser;

import java.time.LocalTime;
import java.time.MonthDay;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChristmasModConfiguration {

	/**
	 * Default start day for christmas features
	 */
	public static final String DEFAULT_START_DAY = "--12-23";

	/**
	 * Default start time for christmas features
	 */
	public static final String DEFAULT_START_TIME = "17:00:00";

	/**
	 * Default end day for christmas features
	 */
	public static final String DEFAULT_END_DAY = "--12-29";

	/**
	 * Default end time for christmas features
	 */
	public static final String DEFAULT_END_TIME = "06:00:00";

	private static final Logger LOGGER = Logger.getLogger(ChristmasMod.class.getName());

	private final Map<String, Integer> presents = new HashMap<>();

	private MonthDay startDay = MonthDay.parse(DEFAULT_START_DAY);
	private LocalTime startTime = LocalTime.parse(DEFAULT_START_TIME);
	private MonthDay endDay = MonthDay.parse(DEFAULT_END_DAY);
	private LocalTime endTime = LocalTime.parse(DEFAULT_END_TIME);

	/**
	 * Configure ChristmasMod
	 * 
	 * Set present itemids for each year
	 * Set start and end day and time
	 * 
	 * @param properties Properties
	 */
	public void configure(Properties properties) {
		ItemIdParser itemIdParser = new ItemIdParser();
		for (String key : properties.stringPropertyNames()) {
			if (key.matches("^present[0-9]{4}$")) {
				int itemId = itemIdParser.parse(properties.getProperty(key));
				presents.put(key, itemId);
				LOGGER.log(Level.INFO, key + ": " + itemId);
			}
		}

		startDay = MonthDay.parse(properties.getProperty("startDay", DEFAULT_START_DAY));
		startTime = LocalTime.parse(properties.getProperty("startTime", DEFAULT_START_TIME));
		endDay = MonthDay.parse(properties.getProperty("endDay", DEFAULT_END_DAY));
		endTime = LocalTime.parse(properties.getProperty("endTime", DEFAULT_END_TIME));
	}

	public MonthDay getStartDay() {
		return startDay;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public MonthDay getEndDay() {
		return endDay;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	/**
	 * Create a ChristmasModCalendar from the configured start and end day and time.
	 * 
	 * @return new ChristmasModCalendar
	 */
	public ChristmasModCalendar createCalendar() {
		return new ChristmasModCalendar(startDay, startTime, endDay, endTime);
	}

	/**
	 * Get the configured present item id for a year
	 * @param year
	 * @return itemid or null
	 */
	public Integer getPresentItemId(int year) {
		String key = "present" + year;
		return presents.get(key);
	}

}
