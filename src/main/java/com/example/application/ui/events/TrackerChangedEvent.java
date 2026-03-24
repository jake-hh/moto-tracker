package com.example.application.ui.events;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;


public class TrackerChangedEvent extends ComponentEvent<UI> {
	// marker event – no payload needed

	public TrackerChangedEvent(UI source) {
		super(source, false); // false = not from client
	}
}
