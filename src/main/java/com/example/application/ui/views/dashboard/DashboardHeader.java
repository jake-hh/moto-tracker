package com.example.application.ui.views.dashboard;

import com.example.application.data.VehicleType;
import com.example.application.data.entity.Vehicle;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Optional;

public class DashboardHeader extends VerticalLayout {

	private final HorizontalLayout nameBar = new HorizontalLayout();
	private final H1          vehicleName = new H1();
	private final NativeLabel vehicleData = new NativeLabel();

	public DashboardHeader() {
		nameBar.setAlignItems(FlexComponent.Alignment.END);

		add(nameBar, vehicleData);
	}

	public void update(Optional<Vehicle> vehicle) {
		vehicleName.setText(
				vehicle.map(Vehicle::toStringShort).orElse("No vehicle has been selected")
		);
		vehicleData.setText(
				vehicle.map(Vehicle::toString).orElse(" ")
		);

		Component icon = vehicle.map(Vehicle::getType)
				.orElse(VehicleType.Other)
				.getIcon();

		icon.getStyle()
				.set("cursor", "default")
				.set("font-size", "2em");

		nameBar.removeAll();
		nameBar.add(icon, vehicleName);
	}
}
