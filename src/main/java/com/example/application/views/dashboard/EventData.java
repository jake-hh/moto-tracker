package com.example.application.views.dashboard;

import com.example.application.data.BasicInterval;
import com.example.application.data.Pair;
import com.example.application.data.Tracker;
import com.vaadin.flow.data.renderer.TextRenderer;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;


public record EventData(Map<Long, Pair<LocalDate, Integer>> dataMap) {

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
		return Optional.ofNullable(tracker.getInterval())
				.map(BasicInterval::toPeriod)
				.flatMap(interval ->
						getLastDate(tracker) //.orElse(MainService.getDateToday())
								.map(lastDate -> lastDate.plus(interval))
				);
	}

	public Optional<Integer> getNextMileage(Tracker tracker) {
		return Optional.ofNullable(tracker.getRange())
				.flatMap(range ->
						getLastMileage(tracker) //.orElse(0)
								.map(lastMileage -> lastMileage + range)
				);
	}

	public <T> TextRenderer<Tracker> render(Function<Tracker, Optional<T>> convert) {
		return new TextRenderer<>(tracker ->
				convert.apply(tracker).map(String::valueOf).orElse("")
		);
	}
}
