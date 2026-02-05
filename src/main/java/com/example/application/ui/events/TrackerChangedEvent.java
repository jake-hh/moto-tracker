package com.example.application.ui.events;

import com.example.application.ui.views.MainLayout;
import com.vaadin.flow.component.ComponentEvent;


public class TrackerChangedEvent extends ComponentEvent<MainLayout> {
	// marker event – no payload needed

	public TrackerChangedEvent(MainLayout source) {
		super(source, false); // false = not from client
	}
}
