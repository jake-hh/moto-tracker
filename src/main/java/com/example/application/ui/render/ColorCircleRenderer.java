package com.example.application.ui.render;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;


public class ColorCircleRenderer {

	private static final double DEFAULT_EM = 1.25;

	public static Component getCircle(String color, double size) {
		Div circle = new Div();
		circle.addClassName("mt-color-circle");
		circle.setWidth(size + "em");
		circle.setHeight(size + "em");
		circle.getStyle().set("background-color", color != null ? color : "transparent");
		return circle;
	}

	public static Component getCircle(String color) {
		return getCircle(color, DEFAULT_EM);
	}

	/* icon wrapper
		Div wrapper = new Div();
		wrapper.addClassName("mt-color-circle-wrapper");
		wrapper.add(VehicleIconRenderer.getIconByVehicleType(VehicleType.Lorry));
	*/
}
