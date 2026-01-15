package com.example.application.views.dashboard;

import com.example.application.data.*;
import com.example.application.services.MainService;
import com.vaadin.flow.data.renderer.TextRenderer;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;


public record EventData(Map<Long, Pair<LocalDate, Integer>> dataMap, Vehicle vehicle, Optional<Event> firstEvent) {

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

	public TextRenderer<Tracker> renderFromStr(Function<Tracker, Optional<String>> convert) {
		return new TextRenderer<>(tracker ->
				convert.apply(tracker).orElse("")
		);
	}

	public <T> TextRenderer<Tracker> render(Function<Tracker, Optional<T>> convert) {
		return new TextRenderer<>(tracker ->
				convert.apply(tracker).map(String::valueOf).orElse("")
		);
	}
}
