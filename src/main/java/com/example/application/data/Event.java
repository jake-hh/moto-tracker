package com.example.application.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;

// import java.time.LocalDate;


@Entity
public class Event extends AbstractEntity {

	@NotNull
	@Column(name = "date_value")
	private String date;
	// private LocalDate date;

	private Integer mileage;


	public String getDateStr() {
		return date;  //.toString();
	}

	public String getDate() {
		return date;
	}

	public Integer getMileage() {
		return mileage;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setMileage(Integer mileage) {
		this.mileage = mileage;
	}
}
