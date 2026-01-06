package com.example.application.event;

import com.example.application.views.vehicle.VehicleView;

import com.vaadin.flow.component.ComponentEvent;


public class VehicleChangedEvent extends ComponentEvent<VehicleView> {
	// marker event – no payload needed

	public VehicleChangedEvent(VehicleView source) {
		super(source, false); // false = not from client
	}
}
