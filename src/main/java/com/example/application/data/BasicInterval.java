package com.example.application.data;

import com.example.application.util.Time;
import jakarta.persistence.Embeddable;
import java.time.Period;


@Embeddable
public record BasicInterval(Integer amount, Unit unit) {

	public static final int AMOUNT_MIN = 1;
	public static final int AMOUNT_MAX = 100;

	public static final String AMOUNT_MIN_MSG = "Interval amount must be greater than 0";
	public static final String AMOUNT_MAX_MSG = "Interval amount is too large";
	public static final String NOT_VALID_MSG = "Interval amount and unit must both be set or both empty";

	public enum Unit {
		Days, Weeks, Months, Years
	}

	public Period toPeriod() {
		return switch (unit) {
			case Days -> Period.ofDays(amount);
			case Weeks -> Period.ofDays(amount * 7);
			case Months -> Period.ofMonths(amount);
			case Years -> Period.ofYears(amount);
		};
	}

	public static Integer toDays(BasicInterval interval) {
		if (interval == null)
			return null;

		return switch (interval.unit) {
			case Days -> interval.amount;
			case Weeks -> interval.amount * Time.DAYS_IN_WEEK;
			case Months -> interval.amount * Time.DAYS_IN_MONTH;
			case Years -> interval.amount * Time.DAYS_IN_YEAR;
		};
	}

	public String toString() {
		return amount + " " + (amount == 1 ? getUnitSingularName() : unit.name());
	}

	private String getUnitSingularName() {
		return unit.name().substring(0, unit.name().length() - 1);
	}

	public static boolean isValid(BasicInterval i) {
		return i == null || i.amount != null && (i.unit != null || i.amount < AMOUNT_MIN || i.amount > AMOUNT_MAX);
	}
}
