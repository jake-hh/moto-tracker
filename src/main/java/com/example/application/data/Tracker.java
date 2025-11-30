package com.example.application.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

// import java.time.Period;


@Entity
public class Tracker extends AbstractEntity {

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


	@Override
	public String toString() {
		return "Tracker " + getName();
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
