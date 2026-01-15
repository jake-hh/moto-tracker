package com.example.application.services.model;

import com.example.application.data.Pair;
import com.example.application.data.BasicInterval;
import com.example.application.data.entity.*;
import com.example.application.services.MainService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;


public record EventData(Map<Long, Pair<LocalDate, Integer>> dataMap, Vehicle vehicle, Optional<Event> firstEvent) {

	public enum Status {
		OK,
		APPROACHING,
		OVERDUE,
		UNKNOWN
	}

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

	//public Optional<Period> getNextDateRelativePeriod(Tracker tracker) {
	//	return getNextDate(tracker).map(nextDate -> Period.between(MainService.getDateToday(), nextDate));
	//}

	public Optional<Long> getNextDateRelativeDays(Tracker tracker) {
		return getNextDate(tracker).map(nextDate -> MainService.getDateToday().until(nextDate, ChronoUnit.DAYS));
	}

	public Optional<Integer> getNextMileageRelative(Tracker tracker) {
		return getNextMileage(tracker).map(nextMileage -> nextMileage - vehicle.getMileage());
	}

	public Status getDateStatus(Tracker tracker) {
		Optional<Long> days = getNextDateRelativeDays(tracker);

		if (days.isEmpty())
			return Status.UNKNOWN;
		if (days.get() < 0)
			return Status.OVERDUE;
		if (days.get() < 90)
			return Status.APPROACHING;
		else
			return Status.OK;
	}

	public Status getMileageStatus(Tracker tracker) {
		Optional<Integer> distance = getNextMileageRelative(tracker);

		if (distance.isEmpty())
			return Status.UNKNOWN;
		if (distance.get() < 0)
			return Status.OVERDUE;
		if (distance.get() < 5000)
			return Status.APPROACHING;
		else
			return Status.OK;
	}
}
