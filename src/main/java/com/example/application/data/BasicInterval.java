package com.example.application.data;

import jakarta.persistence.Embeddable;
import java.time.Period;


@Embeddable
public record BasicInterval(int amount, Unit unit) {

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

	public String toString() {
		return amount + " " + (amount == 1 ? unit.name().substring(0, unit.name().length() - 1) : unit.name());
	}

	public static void updateAmount(Tracker tracker, int amount) {
		BasicInterval current = tracker.getInterval();
		var unit = current != null ? current.unit() : BasicInterval.Unit.Years;
		tracker.setInterval(new BasicInterval(amount, unit));
	}

	public static void updateUnit(Tracker tracker, BasicInterval.Unit unit) {
		BasicInterval current = tracker.getInterval();
		var amount = current != null ? current.amount() : 1;
		tracker.setInterval(new BasicInterval(amount, unit));
	}
}
