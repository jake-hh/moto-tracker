package com.example.application.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// import java.time.Period;


@Entity
public class Tracker extends AbstractEntity {

	@NotNull
	@ManyToOne
	private Vehicle vehicle;

	@NotBlank
	private String name;

	// @Column(name = "interval_value")
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "amount", column = @Column(name = "INTERV_AMOUNT")),
			@AttributeOverride(name = "unit",   column = @Column(name = "INTERV_UNIT"))
	})
	private BasicInterval interv;

	private Integer range;


	public Tracker() { }

	public Tracker(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	@Override
	public String toString() {
		return "Tracker " + getName();
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public String getName() {
		return name;
	}

	public BasicInterval getInterval() {
		return interv;
	}

	public Integer getRange() {
		return range;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setInterval(BasicInterval interv) {
		this.interv = interv;
	}

	public void setRange(Integer range) {
		this.range = range;
	}
}
