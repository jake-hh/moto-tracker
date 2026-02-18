package com.example.application.ui.components;

public class Button extends com.vaadin.flow.component.button.Button {

	public Button(String label) {
		super(label);
	}

	public void setActive(boolean active) {
		setClassName("mt-inactive-btn", !active);
	}
}
