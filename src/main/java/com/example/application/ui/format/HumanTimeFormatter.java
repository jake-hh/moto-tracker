package com.example.application.ui.format;


public final class HumanTimeFormatter {

	private static final int DAYS_IN_YEAR = 365;
	private static final int DAYS_IN_MONTH = 30;
	private static final int DAYS_IN_WEEK = 7;

	public static String formatRounded(long days) {
		if (days == 0)
			return "today";

		else if (days == 1)
			return "tomorrow";

		if (Math.abs(days) >= DAYS_IN_YEAR)
			return format(round(days, DAYS_IN_YEAR), "year");

		if (Math.abs(days) >= DAYS_IN_MONTH)
			return format(round(days, DAYS_IN_MONTH), "month");

		if (Math.abs(days) >= DAYS_IN_WEEK)
			return format(round(days, DAYS_IN_WEEK), "week");

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
