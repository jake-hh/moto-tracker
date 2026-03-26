package com.example.application.data.entity;

import jakarta.persistence.Entity;


@Entity
public class DefaultTracker extends TrackerBase {

	@Override
	public String toString() {
		return "Default Tracker " + getName();
	}
}
