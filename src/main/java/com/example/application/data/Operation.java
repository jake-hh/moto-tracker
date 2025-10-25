package com.example.application.data;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;


@Entity
public class Operation extends AbstractEntity {

	@NotNull
	@ManyToOne
	private Tracker tracker;

	@NotNull
	@ManyToOne
	private Event event;

	// @Override
	// public String toString() {
	// 	return firstName + " " + lastName;
	// }

	public Tracker getTracker() {
		return tracker;
	}

	public void setTracker(Tracker tracker) {
		this.tracker = tracker;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}
}
