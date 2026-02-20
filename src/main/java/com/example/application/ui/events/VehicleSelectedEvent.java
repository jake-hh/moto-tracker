package com.example.application.ui.events;

import com.example.application.data.entity.Vehicle;
import com.example.application.ui.views.MainLayout;

import com.vaadin.flow.component.ComponentEvent;


public class VehicleSelectedEvent extends ComponentEvent<MainLayout> {

	private final Vehicle selectedVehicle;


	public VehicleSelectedEvent(MainLayout source, Vehicle selectedVehicle) {
		super(source, false); // false = not from client
		this.selectedVehicle = selectedVehicle;
	}

	public Vehicle getSelectedVehicle() {
		return selectedVehicle;
	}
}
