package com.example.application.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

// import java.time.Period;


@Entity
public class Tracker extends AbstractEntity {

	@NotBlank
	private String name;

	@Column(name = "interval_value")
	private String interval;
	// private Period interval;

	private Integer range;


	@Override
	public String toString() {
		return "Tracker " + getName();
	}

	public String getName() {
		return name;
	}

	public String getInterval() {
		return interval;
	}

	public Integer getRange() {
		return range;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}

	public void setRange(Integer range) {
		this.range = range;
	}
}
