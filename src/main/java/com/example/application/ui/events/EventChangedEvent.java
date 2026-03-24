package com.example.application.ui.events;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;


public class EventChangedEvent extends ComponentEvent<UI> {
	// marker event - no payload needed

	public EventChangedEvent(UI source) {
		super(source, false); // false = not from client
	}
}
