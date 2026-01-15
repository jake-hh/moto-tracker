package com.example.application.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;


@Entity
public class Event extends AbstractEntity {

	@NotNull
	@ManyToOne
	private Vehicle vehicle;

	@NotNull
	@Column(name = "date_value")
	private LocalDate date;

	private Integer mileage;


	public Event() { }

	public Event(Vehicle vehicle, LocalDate date) {
		this.vehicle = vehicle;
		this.date = date;
	}

	@Override
	public String toString() {
		return "Event " + getDateStr() + " " + getMileage();
	}

	public String getDateStr() {
		return date.toString();
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public LocalDate getDate() {
		return date;
	}

	public Integer getMileage() {
		return mileage;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public void setMileage(Integer mileage) {
		this.mileage = mileage;
	}
}
