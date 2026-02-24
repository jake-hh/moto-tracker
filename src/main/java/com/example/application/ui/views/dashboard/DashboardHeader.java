package com.example.application.ui.views.dashboard;

import com.example.application.data.VehicleType;
import com.example.application.data.entity.Vehicle;
import com.example.application.ui.render.ColorCircleRenderer;
import com.example.application.ui.render.VehicleIconRenderer;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Optional;


public class DashboardHeader extends VerticalLayout {

	private final HorizontalLayout nameBar = new HorizontalLayout();
	private final H1          vehicleName = new H1();
	private final NativeLabel vehicleData = new NativeLabel();


	public DashboardHeader() {
		nameBar.setAlignItems(Alignment.CENTER);

		add(nameBar, vehicleData);
	}

	public void update(Optional<Vehicle> vehicle) {
		vehicleName.setText(
				vehicle.map(Vehicle::toStringShort).orElse("No vehicle has been selected")
		);
		vehicleData.setText(
				vehicle.map(Vehicle::toString).orElse(" ")
		);

		Component icon = VehicleIconRenderer.getIconByVehicleType(
				vehicle.map(Vehicle::getType).orElse(VehicleType.Other)
		);
		VehicleIconRenderer.addSize(icon, 2);

		nameBar.removeAll();
		nameBar.add(icon, vehicleName);

		vehicle.map(Vehicle::getColour)
				.ifPresent(color ->
						nameBar.add(ColorCircleRenderer.getCircle(color, 1.25)));
	}
}
