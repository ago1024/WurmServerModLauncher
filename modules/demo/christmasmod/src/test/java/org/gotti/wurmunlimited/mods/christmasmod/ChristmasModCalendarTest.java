package org.gotti.wurmunlimited.mods.christmasmod;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;

public class ChristmasModCalendarTest {


	MonthDay startDay = MonthDay.parse("--12-23");
	LocalTime startTime = LocalTime.parse("17:00:00");
	MonthDay endDay = MonthDay.parse("--12-29");
	LocalTime endTime = LocalTime.parse("06:00:00");

	LocalDateTime before = LocalDateTime.parse("2020-12-20T00:00:00");
	LocalDateTime start = LocalDateTime.parse("2020-12-23T17:00:00");
	LocalDateTime between = LocalDateTime.parse("2020-12-25T05:00:00");
	LocalDateTime end = LocalDateTime.parse("2020-12-29T06:00:00");
	LocalDateTime after = LocalDateTime.parse("2020-12-30T00:00:00");

	@Test
	public void testIsBeforeChristmas() {
		ChristmasModCalendar calendar = new ChristmasModCalendar(startDay, startTime, endDay, endTime);
		Assertions.assertThat(calendar.isBeforeChristmas()).isEqualTo(calendar.isBeforeChristmas(LocalDateTime.now()));
	}

	@Test
	public void testIsBeforeChristmasReturnsTrueWhenBefore() {
		ChristmasModCalendar calendar = new ChristmasModCalendar(startDay, startTime, endDay, endTime);
		Assertions.assertThat(calendar.isBeforeChristmas(before)).isTrue();
	}

	@Test
	public void testIsBeforeChristmasReturnsFalseWhenAtStart() {
		ChristmasModCalendar calendar = new ChristmasModCalendar(startDay, startTime, endDay, endTime);
		Assertions.assertThat(calendar.isBeforeChristmas(start)).isFalse();
	}

	@Test
	public void testIsBeforeChristmasReturnsFalseWhenBetween() {
		ChristmasModCalendar calendar = new ChristmasModCalendar(startDay, startTime, endDay, endTime);
		Assertions.assertThat(calendar.isBeforeChristmas(between)).isFalse();
	}

	@Test
	public void testIsBeforeChristmasReturnsFalseWhenAtEnd() {
		ChristmasModCalendar calendar = new ChristmasModCalendar(startDay, startTime, endDay, endTime);
		Assertions.assertThat(calendar.isBeforeChristmas(end)).isFalse();
	}

	@Test
	public void testIsBeforeChristmasReturnsFalseWhenAfter() {
		ChristmasModCalendar calendar = new ChristmasModCalendar(startDay, startTime, endDay, endTime);
		Assertions.assertThat(calendar.isBeforeChristmas(after)).isFalse();
	}

	@Test
	public void testIsAfterChristmas() {
		ChristmasModCalendar calendar = new ChristmasModCalendar(startDay, startTime, endDay, endTime);
		Assertions.assertThat(calendar.isAfterChristmas()).isEqualTo(calendar.isAfterChristmas(LocalDateTime.now()));
	}

	@Test
	public void testIsAfterChristmasReturnsFalseWhenBefore() {
		ChristmasModCalendar calendar = new ChristmasModCalendar(startDay, startTime, endDay, endTime);
		Assertions.assertThat(calendar.isAfterChristmas(before)).isFalse();
	}

	@Test
	public void testIsAfterChristmasReturnsFalseWhenAtStart() {
		ChristmasModCalendar calendar = new ChristmasModCalendar(startDay, startTime, endDay, endTime);
		Assertions.assertThat(calendar.isAfterChristmas(start)).isFalse();
	}

	@Test
	public void testIsAfterChristmasReturnsFalseWhenBetween() {
		ChristmasModCalendar calendar = new ChristmasModCalendar(startDay, startTime, endDay, endTime);
		Assertions.assertThat(calendar.isAfterChristmas(between)).isFalse();
	}

	@Test
	public void testIsAfterChristmasReturnsFalseWhenAtEnd() {
		ChristmasModCalendar calendar = new ChristmasModCalendar(startDay, startTime, endDay, endTime);
		Assertions.assertThat(calendar.isAfterChristmas(end)).isFalse();
	}

	@Test
	public void testIsAfterChristmasReturnsTrueWhenAfter() {
		ChristmasModCalendar calendar = new ChristmasModCalendar(startDay, startTime, endDay, endTime);
		Assertions.assertThat(calendar.isAfterChristmas(after)).isTrue();
	}

	@Test
	public void testIsChristmas() {
		ChristmasModCalendar calendar = new ChristmasModCalendar(startDay, startTime, endDay, endTime);
		Assertions.assertThat(calendar.isChristmas()).isEqualTo(calendar.isChristmas(LocalDateTime.now()));
	}

	@Test
	public void testisChristmasReturnsFalseWhenBefore() {
		ChristmasModCalendar calendar = new ChristmasModCalendar(startDay, startTime, endDay, endTime);
		Assertions.assertThat(calendar.isChristmas(before)).isFalse();
	}

	@Test
	public void testisChristmasReturnsTrueWhenAtStart() {
		ChristmasModCalendar calendar = new ChristmasModCalendar(startDay, startTime, endDay, endTime);
		Assertions.assertThat(calendar.isChristmas(start)).isTrue();
	}

	@Test
	public void testisChristmasReturnsTrueWhenBetween() {
		ChristmasModCalendar calendar = new ChristmasModCalendar(startDay, startTime, endDay, endTime);
		Assertions.assertThat(calendar.isChristmas(between)).isTrue();
	}

	@Test
	public void testisChristmasReturnsTrueWhenAtEnd() {
		ChristmasModCalendar calendar = new ChristmasModCalendar(startDay, startTime, endDay, endTime);
		Assertions.assertThat(calendar.isChristmas(end)).isTrue();
	}

	@Test
	public void testisChristmasReturnsFalseWhenAfter() {
		ChristmasModCalendar calendar = new ChristmasModCalendar(startDay, startTime, endDay, endTime);
		Assertions.assertThat(calendar.isChristmas(after)).isFalse();
	}

	
}
