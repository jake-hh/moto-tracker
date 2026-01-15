package com.example.application.ui.format;

import java.util.Locale;


public final class HumanDistanceFormatter {

	public static String format(Integer distance) {
		var sb = new StringBuilder();

		if (distance > 0)
			sb.append("+ ");
		else if (distance < 0)
			sb.append("- ");

		sb.append(String.format(Locale.FRANCE, "%,d", Math.abs(distance)));
		sb.append(" km");

		return sb.toString();
	}
}
