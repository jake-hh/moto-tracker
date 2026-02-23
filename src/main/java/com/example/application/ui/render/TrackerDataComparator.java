package com.example.application.ui.render;

import com.example.application.data.DashboardEventFormat;
import com.example.application.data.entity.Tracker;
import com.example.application.services.model.TrackerData;

import java.time.LocalDate;


public class TrackerDataComparator {

	public static LocalDate compareDate(TrackerData data, DashboardEventFormat format, Tracker tracker) {

		if (format == DashboardEventFormat.LAST_SERVICE)
			return data.getLastDate(tracker).orElse(null);
		else
			return data.getNextDate(tracker).orElse(null);
	}

	public static Integer compareMileage(TrackerData data, DashboardEventFormat format, Tracker tracker) {

		if (format == DashboardEventFormat.LAST_SERVICE)
			return data.getLastMileage(tracker).orElse(null);
		else
			return data.getNextMileage(tracker).orElse(null);
	}}
