package com.example.application.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.*;

import java.time.LocalDate;


@SuppressWarnings("unused")
@Entity
public class Vehicle extends AbstractEntity {

	public static final int MILEAGE_STEP = 100;
	public static final int MIN_MILEAGE = 100;
	public static final int MAX_MILEAGE = 10_000_000;

	public static final String MILEAGE_STEP_MSG = "Number must be a multiple of " + MILEAGE_STEP;
	public static final String MILEAGE_MIN_MSG = "Vehicle mileage must be at least " + MIN_MILEAGE + " km";
	public static final String MILEAGE_MAX_MSG = "Vehicle mileage is too large";

	@ManyToOne(optional = false)
	@JoinColumn(name = "owner_id")
    private AppUser owner;

	//@Size(min = 2, max = 24, message = "Vehicle type must be 2–24 characters")
	@NotBlank(message = "Vehicle type is required")
	@Pattern(regexp = "[\\p{L}]*", message = "Vehicle type must contain letters")
    private String type; //osobowy PKV, dostawczy LKV, motorbike

	@NotBlank(message = "Vehicle make is required")
	@Pattern(regexp = "[\\p{L} -]*", message = "Vehicle make must contain letters, space or dash")
    private String make; //comp

	@NotBlank(message = "Vehicle model is required")
	@Pattern(regexp = "[\\p{L}0-9 -]*", message = "Vehicle model must contain alphanumerics space or dash")
    private String model;

	@NotBlank(message = "Vehicle engine is required")
	private String engine;

	@NotBlank(message = "Vehicle colour is required")
	@Pattern(regexp = "[\\p{L}]*", message = "Vehicle colour must contain letters")
    private String colour;

	@Pattern(regexp = "[\\p{L}0-9]*", message = "Vehicle plate must contain alphanumerics")
    private String plate;

	@Pattern(regexp = "[\\p{L}0-9]*", message = "Vehicle VIN number must contain alphanumerics")
    private String vin;

	@NotNull(message = "Vehicle mileage is required")
	@Min(value = 100, message = MILEAGE_MIN_MSG)
	@Max(value = 10_000_000, message = MILEAGE_MAX_MSG)
	private Integer mileage;

    private LocalDate productionDate;

	private LocalDate registrationDate;

	@NotNull
	private LocalDate trackingDate;


	public Vehicle() { }

	public Vehicle(AppUser owner, LocalDate trackingDate) {
		this.owner = owner;
		this.trackingDate = trackingDate;
	}

	@Override
	public String toString() {
		return "Vehicle of " + owner + " [ " + type + " " + make + " " + model + " " + engine + " " + colour + " " + plate + " " + mileage + " " + trackingDate + " ]";
	}

	public String toStringShort() {
		return make + " " + model;
	}

	public AppUser getOwner() {
		return owner;
	}

	public String getType() {
		return type;
	}

	public String getMake() {
		return make;
	}

	public String getModel() {
		return model;
	}

	public String getEngine() {
		return engine;
	}

	public String getColour() {
		return colour;
	}

	public String getPlate() {
		return plate;
	}

	public String getVin() {
		return vin;
	}

	public Integer getMileage() {
		return mileage;
	}

	public LocalDate getProductionDate() {
		return productionDate;
	}

	public LocalDate getRegistrationDate() {
		return registrationDate;
	}

	public LocalDate getTrackingDate() {
		return trackingDate;
	}

	public void setOwner(AppUser owner) {
		this.owner = owner;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public void setEngine(String engine) {
		this.engine = engine;
	}

	public void setColour(String colour) {
		this.colour = colour;
	}

	public void setPlate(String plate) {
		this.plate = plate;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public void setMileage(Integer mileage) {
		this.mileage = mileage;
	}

	public void setProductionDate(LocalDate productionDate) {
		this.productionDate = productionDate;
	}

	public void setRegistrationDate(LocalDate registrationDate) {
		this.registrationDate = registrationDate;
	}

	public  void setTrackingDate(LocalDate trackingDate) {
		this.trackingDate = trackingDate;
	}
}
