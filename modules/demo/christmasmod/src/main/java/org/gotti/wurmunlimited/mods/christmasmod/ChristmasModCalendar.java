package org.gotti.wurmunlimited.mods.christmasmod;

import org.gotti.wurmunlimited.modloader.callbacks.CallbackApi;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.Year;
import java.util.Objects;

public class ChristmasModCalendar {
	
	private final MonthDay startDay;
	private final LocalTime startTime;
	private final MonthDay endDay;
	private final LocalTime endTime;

	public ChristmasModCalendar(MonthDay startDay, LocalTime startTime, MonthDay endDay, LocalTime endTime) {
		this.startDay = Objects.requireNonNull(startDay);
		this.startTime = Objects.requireNonNull(startTime);
		this.endDay = Objects.requireNonNull(endDay);
		this.endTime = Objects.requireNonNull(endTime);
	}

	@CallbackApi
	public boolean isChristmas() {
		return isChristmas(LocalDateTime.now());
	}

	public boolean isChristmas(LocalDateTime now) {
		return !this.isBeforeChristmas(now) && !this.isAfterChristmas(now);
	}

	@CallbackApi
	public boolean isBeforeChristmas() {
		return isBeforeChristmas(LocalDateTime.now());
	}

	public boolean isBeforeChristmas(LocalDateTime now) {
		LocalDateTime startDateTime = Year.from(now).atMonthDay(startDay).atTime(startTime);
		return now.isBefore(startDateTime);
	}

	@CallbackApi
	public boolean isAfterChristmas() {
		return isAfterChristmas(LocalDateTime.now());
	}

	public boolean isAfterChristmas(LocalDateTime now) {
		LocalDateTime endDateTime = Year.from(now).atMonthDay(endDay).atTime(endTime);
		return now.isAfter(endDateTime);
	}

}
