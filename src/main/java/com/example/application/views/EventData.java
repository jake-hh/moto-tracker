package com.example.application.views;

import com.example.application.data.Pair;
import com.example.application.data.Tracker;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;


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

	public String getLastDateString(Tracker tracker) {
		return getLastDate(tracker).map(String::valueOf).orElse("-");
	}

	public String getLastMileageString(Tracker tracker) {
		return getLastMileage(tracker).map(String::valueOf).orElse("-");
	}
}
