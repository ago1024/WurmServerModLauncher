package org.gotti.wurmunlimited.mods.christmasmod;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.time.LocalTime;
import java.time.MonthDay;
import java.util.Properties;

public class ChristmasModConfigurationTest {

	private ChristmasModConfiguration configuration = new ChristmasModConfiguration();
	private Properties properties = new Properties();

	@Test
	public void testConfigurePresents() {
		properties.setProperty("present2015", "123");
		properties.setProperty("present2017", "456");
		properties.setProperty("present2018", "789");
		configuration.configure(properties);

		Assertions.assertThat(configuration.getPresentItemId(2015)).isEqualTo(123);
		Assertions.assertThat(configuration.getPresentItemId(2017)).isEqualTo(456);
		Assertions.assertThat(configuration.getPresentItemId(2018)).isEqualTo(789);
		Assertions.assertThat(configuration.getPresentItemId(2016)).isNull();
	}

	@Test
	public void testConfigurePresentsWithItemNames() {
		properties.setProperty("present2015", "spyglass");
		properties.setProperty("present2017", "scrapwood");
		configuration.configure(properties);

		Assertions.assertThat(configuration.getPresentItemId(2015)).isEqualTo(489);
		Assertions.assertThat(configuration.getPresentItemId(2017)).isEqualTo(169);
	}

	@Test
	public void testConfigureStartDay() {
		properties.setProperty("startDay", "--12-20");
		configuration.configure(properties);
		Assertions.assertThat(configuration.getStartDay()).isEqualTo(MonthDay.parse("--12-20"));
	}

	@Test
	public void testConfigureStartTime() {
		properties.setProperty("startTime", "08:00:00");
		configuration.configure(properties);
		Assertions.assertThat(configuration.getStartTime()).isEqualTo(LocalTime.parse("08:00:00"));
	}

	@Test
	public void testConfigureEndDay() {
		properties.setProperty("endDay", "--12-30");
		configuration.configure(properties);
		Assertions.assertThat(configuration.getEndDay()).isEqualTo(MonthDay.parse("--12-30"));
	}

	@Test
	public void testConfigureEndTime() {
		properties.setProperty("endTime", "18:00:00");
		configuration.configure(properties);
		Assertions.assertThat(configuration.getEndTime()).isEqualTo(LocalTime.parse("18:00:00"));
	}

}
