package com.example.application.data.entity;

import jakarta.persistence.Entity;


@Entity
public class DefaultTracker extends AbstractTracker {

	@Override
	public String toString() {
		return "Default Tracker " + getName();
	}

	public void passValues(Tracker tracker) {
		tracker.setName(getName());
		tracker.setInterval(getInterval());
		tracker.setRange(getRange());
	}
}
