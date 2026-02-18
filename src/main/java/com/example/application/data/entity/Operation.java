package com.example.application.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;


@Entity
public class Operation extends AbstractEntity {

	@NotNull(message = "Tracker is required")
	@ManyToOne
	private Tracker tracker;

	@NotNull(message = "Event is required")
	@ManyToOne
	private Event event;


	public Operation() {}

	public Operation(Event event, Tracker tracker) {
		this.event = event;
		this.tracker = tracker;
	}

	@Override
	public String toString() {
		return "Operation [ " + getEvent() + " <-> " + getTracker() + " ]";
	}

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
