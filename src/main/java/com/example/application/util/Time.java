package com.example.application.util;

import java.time.LocalDate;
import java.time.ZoneId;


public final class Time {
	public static final int DAYS_IN_YEAR = 365;
	public static final int DAYS_IN_MONTH = 30;
	public static final int DAYS_IN_WEEK = 7;

	public static LocalDate today() {
		return LocalDate.now(ZoneId.systemDefault());
	}
}
