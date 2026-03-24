package com.example.application.ui.events;

import com.example.application.data.entity.Vehicle;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;


public class VehicleSelectedEvent extends ComponentEvent<UI> {

	private final Vehicle selectedVehicle;


	public VehicleSelectedEvent(UI source, Vehicle selectedVehicle) {
		super(source, false); // false = not from client
		this.selectedVehicle = selectedVehicle;
	}

	public Vehicle getSelectedVehicle() {
		return selectedVehicle;
	}
}
