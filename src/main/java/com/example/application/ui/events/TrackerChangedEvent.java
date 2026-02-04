package com.example.application.ui.events;

import com.example.application.ui.views.tracker.TrackerView;
import com.vaadin.flow.component.ComponentEvent;


public class TrackerChangedEvent extends ComponentEvent<TrackerView> {
	// marker event – no payload needed

	public TrackerChangedEvent(TrackerView source) {
		super(source, false); // false = not from client
	}
}
