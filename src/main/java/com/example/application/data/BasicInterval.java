package com.example.application.data;

import jakarta.persistence.Embeddable;
import java.time.Period;


@Embeddable
public record BasicInterval(Integer amount, Unit unit) {

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
		return amount + " " + (amount == 1 ? getUnitSingularName() : unit.name());
	}

	private String getUnitSingularName() {
		return unit.name().substring(0, unit.name().length() - 1);
	}

	public boolean isValid() {
		return amount != null && unit != null && amount() > 0;
	}
}
