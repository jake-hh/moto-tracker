package com.example.application.ui.events;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;


public class OperationChangedEvent extends ComponentEvent<UI> {
	boolean createdInEventItem;

	public OperationChangedEvent(UI source, boolean createdInEventItem) {
		super(source, false); // false = not from client
		this.createdInEventItem = createdInEventItem;
	}

	public boolean wasCreatedInEventItem() {
		return createdInEventItem;
	}
}
