package com.example.application.services.model;

import com.example.application.data.Pair;
import com.example.application.data.BasicInterval;
import com.example.application.data.entity.*;
import com.example.application.util.Time;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;


public record TrackerData(Map<Long, Pair<LocalDate, Integer>> dataMap, Vehicle vehicle, Optional<Event> firstEvent) {

	private Optional<Pair<LocalDate, Integer>> getPair(Tracker tracker) {
		return Optional.ofNullable(dataMap.get(tracker.getId()));
	}

	public Optional<LocalDate> getLastDate(Tracker tracker) {
		return getPair(tracker).map(Pair::first);
	}

	public Optional<Integer> getLastMileage(Tracker tracker) {
		return getPair(tracker).map(Pair::second);
	}

	public Optional<LocalDate> getNextDate(Tracker tracker) {
		return getLastDate(tracker)
				.or(() -> firstEvent.map(Event::getDate))
				.flatMap(date ->
						Optional.ofNullable(tracker.getInterval())
								.map(BasicInterval::toPeriod)
								.map(date::plus));
	}

	public Optional<Integer> getNextMileage(Tracker tracker) {
		return getLastMileage(tracker)
				.or(() -> firstEvent.map(Event::getMileage))
				.flatMap(mileage ->
						Optional.ofNullable(tracker.getRange())
								.map(range -> mileage + range));
	}

	public Long calculateRemainingDays(LocalDate nextDate) {
		return Time.today().until(nextDate, ChronoUnit.DAYS);
	}

	public Integer calculateRemainingDistance(Integer nextMileage) {
		return nextMileage - vehicle.getMileage();
	}
}
