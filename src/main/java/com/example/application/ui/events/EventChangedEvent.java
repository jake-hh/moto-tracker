package com.example.application.ui.events;

import com.example.application.ui.views.MainLayout;
import com.vaadin.flow.component.ComponentEvent;


public class EventChangedEvent extends ComponentEvent<MainLayout> {
	// marker event - no payload needed

	public EventChangedEvent(MainLayout source) {
		super(source, false); // false = not from client
	}
}
