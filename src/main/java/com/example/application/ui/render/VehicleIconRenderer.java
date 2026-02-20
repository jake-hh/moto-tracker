package com.example.application.ui.render;

import com.example.application.data.VehicleType;
import com.example.application.data.entity.Vehicle;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;


public class VehicleIconRenderer {

	// --- Render menu items ---
	public static Component getDropdownIconsByVehicle(Vehicle vehicle) {
		return getLayout(
				vehicle.getType(),
				vehicle.toStringShort()
		);
	}

	// --- Render menu items ---
	public static Component getDropdownIconsByVehicleType(VehicleType vehicleType) {
		return getLayout(
				vehicleType,
				vehicleType.name()
		);
	}

	// --- Render menu layout ---
	private static Component getLayout(VehicleType vehicleType, String vehicleName) {
		var icon = getIconByVehicleType(vehicleType);
		icon.addClassName("mt-box-item-icon");

		var label = new Span(vehicleName);
		label.getStyle().set("white-space", "nowrap");

		var layout = new HorizontalLayout(
				icon,
				label
		);
		layout.addClassName("mt-box-item-layout");
		layout.setAlignItems(FlexComponent.Alignment.CENTER);
		layout.setPadding(false);
		layout.setSpacing(true);

		return layout;
	}

	// --- Render value icon ---
	public static Component getSelectedVehicleIconByVehicle(Vehicle vehicle) {
		return getSelectedVehicleIconByVehicleType(vehicle == null ? null : vehicle.getType());
	}

	// --- Render value icon ---
	public static Component getSelectedVehicleIconByVehicleType(VehicleType vehicleType) {
		if (vehicleType != null) {
			Component icon = getIconByVehicleType(vehicleType);
			icon.addClassName("mt-box-value-icon");
			return icon;
		}
		else return null;
	}

	// --- Render icon ---
	public static Component getIconByVehicle(Vehicle vehicle) {
		return getIconByVehicleType(vehicle.getType());
	}

	// --- Render icon ---
	public static Component getIconByVehicleType(VehicleType vehicleType) {
		Component icon = vehicleType.getIcon();
		icon.addClassName("mt-box-icon");
		return icon;
	}

	public static void addColor(Component icon, String color) {
		icon.getStyle().set("color", color);
	}

	public static void addSize(Component icon, float size) {
		icon.getStyle().set("font-size", size + "em");
	}
}