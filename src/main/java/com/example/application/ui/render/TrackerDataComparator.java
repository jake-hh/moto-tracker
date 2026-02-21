package com.example.application.ui.render;

import com.example.application.data.DashboardEventFormat;
import com.example.application.data.entity.Tracker;
import com.example.application.services.model.TrackerData;

import java.time.LocalDate;


public class TrackerDataComparator {

	public static LocalDate compareDate(TrackerData data, DashboardEventFormat format, Tracker tracker) {

		if (format == DashboardEventFormat.LAST_SERVICE)
			return data.getLastDate(tracker).orElse(LocalDate.EPOCH);
		else
			return data.getNextDate(tracker).orElse(LocalDate.EPOCH);
	}

	public static long compareMileage(TrackerData data, DashboardEventFormat format, Tracker tracker) {

		if (format == DashboardEventFormat.LAST_SERVICE)
			return data.getLastMileage(tracker).orElse(0);
		else
			return data.getNextMileage(tracker).orElse(0);
	}}
