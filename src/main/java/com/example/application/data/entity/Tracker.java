package com.example.application.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;


@Entity
public class Tracker extends TrackerBase {

	@NotNull
	@ManyToOne(optional = false)
	private Vehicle vehicle;


	public Tracker() { }

	public Tracker(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public static boolean isEmpty(Tracker t) {
		return t == null || t.getVehicle() == null || t.getName() == null || t.getName().isBlank();
	}

	@Override
	public String toString() {
		return "Tracker " + getName();
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}
}
