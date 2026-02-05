package com.example.application.ui.events;

import com.example.application.data.entity.AbstractEntity;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.formlayout.FormLayout;

public class LayoutFormEvent<Entity extends AbstractEntity> extends ComponentEvent<FormLayout> {
	private final Entity value;

	public LayoutFormEvent(FormLayout source, Entity value) {
		super(source, false);
		this.value = value;
	}

	public Entity getValue() {
		return value;
	}
}
