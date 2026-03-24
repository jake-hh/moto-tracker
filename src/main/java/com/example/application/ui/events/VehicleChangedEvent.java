package com.example.application.ui.events;

import com.example.application.data.entity.Vehicle;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;


public class VehicleChangedEvent extends ComponentEvent<UI> {

	private final Vehicle vehicle;

	public VehicleChangedEvent(UI source, Vehicle vehicle) {
		super(source, false);
		this.vehicle = vehicle;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}
}
