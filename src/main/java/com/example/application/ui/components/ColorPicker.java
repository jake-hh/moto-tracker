package com.example.application.ui.components;

import com.example.application.data.entity.Vehicle;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Input;


public class ColorPicker extends CustomField<String> {

	private final Input inputPicker = new Input();


	public ColorPicker() {
		inputPicker.setType("color");
		inputPicker.setValue(Vehicle.DEFAULT_COLOR);
		inputPicker.addClassName("mt-color-picker-input");
		//inputPicker.addValueChangeListener(e -> updateValue());

		Div wrapper = new Div(inputPicker);
		wrapper.addClassName("mt-color-picker-wrapper");
		add(wrapper);
	}

	public ColorPicker(String label) {
		this();
		setLabel(label);
	}

	@Override
	protected String generateModelValue() {
		return inputPicker.getValue();
	}

	@Override
	protected void setPresentationValue(String value) {
		inputPicker.setValue(value != null ? value : Vehicle.DEFAULT_COLOR);
		//updateValue();
	}
}