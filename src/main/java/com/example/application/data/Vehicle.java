package com.example.application.data;

//import jakarta.persistence.ManyToMany;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@SuppressWarnings("unused")
@Entity
public class Vehicle extends AbstractEntity {

	//@NotBlank
	//@ManyToMany
    //private User owner;

	@NotBlank
    private String type; //osobowy PKV, dostawczy LKV, motorbike

	@NotBlank
    private String make; //comp

	@NotBlank
    private String model;

	@NotBlank
	private String engine;

	@NotBlank
    private String colour;

    private String plate;

    private String vin;

    private LocalDate productionDate;

	private LocalDate registrationDate;

	@NotNull
	private LocalDate trackingDate;

	@Override
	public String toString() {
		return "Vehicle [ " + getMake() + " " + getModel() + " " + getEngine() + " " + getColour() + " " + getPlate() + " ]";
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

	public LocalDate getProductionDate() {
		return productionDate;
	}

	public LocalDate getRegistrationDate() {
		return registrationDate;
	}

	public LocalDate getTrackingDate() {
		return trackingDate;
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
