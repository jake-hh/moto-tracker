package com.example.application.data;

import jakarta.persistence.Embeddable;
import java.time.Period;


@Embeddable
public record BasicInterval(int amount, Unit unit) {

	public enum Unit {
		DAYS, WEEKS, MONTHS, YEARS
	}

	public Period toPeriod() {
		return switch (unit) {
			case DAYS -> Period.ofDays(amount);
			case WEEKS -> Period.ofDays(amount * 7);
			case MONTHS -> Period.ofMonths(amount);
			case YEARS -> Period.ofYears(amount);
		};
	}

	public String toString() {
		return amount + " " + unit;
	}

	public static void updateAmount(Tracker tracker, int amount) {
		BasicInterval current = tracker.getInterval();
		var unit = current != null ? current.unit() : BasicInterval.Unit.YEARS;
		tracker.setInterval(new BasicInterval(amount, unit));
	}

	public static void updateUnit(Tracker tracker, BasicInterval.Unit unit) {
		BasicInterval current = tracker.getInterval();
		var amount = current != null ? current.amount() : 1;
		tracker.setInterval(new BasicInterval(amount, unit));
	}
}
