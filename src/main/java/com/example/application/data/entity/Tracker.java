package com.example.application.data.entity;

import com.example.application.data.BasicInterval;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;


@Entity
public class Tracker extends AbstractEntity {

	public static final int RANGE_STEP = 100;
	public static final int RANGE_MIN = 100;
	public static final int RANGE_MAX = 1_000_000;

	public static final String RANGE_STEP_MSG = "Range must be a multiple of " + RANGE_STEP;
	public static final String RANGE_MIN_MSG = "Range must be at least " + RANGE_MIN + " km";
	public static final String RANGE_MAX_MSG = "Range is too large";

	@NotNull
	@ManyToOne(optional = false)
	private Vehicle vehicle;

	@NotBlank(message = "Tracker name is required")
	@Size(max = 24, message = "Tracker name exceeds 24 characters")
	@Pattern(regexp = "[\\p{L}0-9 -]*", message = "Tracker name must contain alphanumerics space or dash")
	private String name;

	@Min(value = 100, message = RANGE_MIN_MSG)
	@Max(value = 10_000_000, message = RANGE_MAX_MSG)
	private Integer range;

	// @Column(name = "interval_value")
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "amount", column = @Column(name = "INTERV_AMOUNT")),
			@AttributeOverride(name = "unit",   column = @Column(name = "INTERV_UNIT"))
	})
	private BasicInterval interv;


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
