package com.example.application.ui.format;

import com.example.application.util.Time;


public final class HumanTimeFormatter {

	public static String formatRounded(long days) {
		if (days == 0)
			return "today";

		else if (days == 1)
			return "tomorrow";

		if (Math.abs(days) >= Time.DAYS_IN_YEAR)
			return format(round(days, Time.DAYS_IN_YEAR), "year");

		if (Math.abs(days) >= Time.DAYS_IN_MONTH)
			return format(round(days, Time.DAYS_IN_MONTH), "month");

		if (Math.abs(days) >= Time.DAYS_IN_WEEK)
			return format(round(days, Time.DAYS_IN_WEEK), "week");

		return format(days, "day");
	}

	private static long round(long days, long unitDays) {
		return Math.round((float) days / unitDays);
	}

	private static String format(long value, String unit) {
		var sb = new StringBuilder();

		//if (value > 0) sb.append("due in ");
		sb.append(Math.abs(value));
		sb.append(" ");
		sb.append(unit);
		sb.append(value == 1 ? "" : "s");

		if (value > 0)
			sb.append(" left");
		else
			sb.append(" overdue");

		return sb.toString();
	}
}
