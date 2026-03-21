package com.example.application.ui.events;

import com.example.application.ui.views.MainLayout;
import com.vaadin.flow.component.ComponentEvent;


public class OperationChangedEvent extends ComponentEvent<MainLayout> {
	boolean createdInEventItem;

	public OperationChangedEvent(MainLayout source, boolean createdInEventItem) {
		super(source, false); // false = not from client
		this.createdInEventItem = createdInEventItem;
	}

	public boolean wasCreatedInEventItem() {
		return createdInEventItem;
	}
}
