package com.example.application.ui.render;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;


public class ColorCircleRenderer {

	private static final float BASE_SIZE_PX = 20;

	public static Component getCircle(String color, float size) {
		Div circle = new Div();
		circle.addClassName("mt-color-circle");
		circle.setWidth(size * BASE_SIZE_PX + "px");
		circle.setHeight(size * BASE_SIZE_PX + "px");
		circle.getStyle().set("background-color", color != null ? color : "transparent");
		return circle;
	}

	public static Component getCircle(String color) {
		return getCircle(color, 1);
	}

	/* icon wrapper
		Div wrapper = new Div();
		wrapper.addClassName("mt-color-circle-wrapper");
		wrapper.add(VehicleIconRenderer.getIconByVehicleType(VehicleType.Lorry));
	*/
}
