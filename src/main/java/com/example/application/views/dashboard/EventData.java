package com.example.application.views.dashboard;

import com.example.application.data.*;
import com.vaadin.flow.data.renderer.TextRenderer;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;


public record EventData(Map<Long, Pair<LocalDate, Integer>> dataMap, Optional<Event> firstEvent) {

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

	public <T> TextRenderer<Tracker> render(Function<Tracker, Optional<T>> convert) {
		return new TextRenderer<>(tracker ->
				convert.apply(tracker).map(String::valueOf).orElse("")
		);
	}
}
