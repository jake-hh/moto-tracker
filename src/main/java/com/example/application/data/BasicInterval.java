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
}
