package com.example.application.ui.views.service;

import com.example.application.data.entity.Event;
import com.example.application.data.entity.Tracker;
import com.example.application.services.MainService;
import com.example.application.ui.events.OperationChangedEvent;
import com.example.application.ui.events.TrackerChangedEvent;
import com.example.application.ui.events.VehicleSelectedEvent;
import com.example.application.ui.views.MainLayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import jakarta.annotation.security.PermitAll;

import java.util.List;


@SpringComponent
@UIScope
@PermitAll
@Route(value = "services", layout = MainLayout.class)
@PageTitle("Services | Moto Tracker")
public class ServiceView extends VerticalLayout {

	private VerticalLayout eventList;
	private final MainService service;
	private final MainLayout mainLayout;

	public ServiceView(MainService service, MainLayout mainLayout) {
		this.service = service;
		this.mainLayout = mainLayout;

		mainLayout.addVehicleSelectedListener(this::onVehicleSelected);
		mainLayout.addTrackerChangedListener(this::onTrackerChanged);
		mainLayout.addOperationChangedListener(this::onOperationChanged);

		addClassName("view");
		//setPadding(true);
		//setSpacing(true);
		setSizeFull();

		createEventList();
		add(createToolbar(), eventList);
	}

	private void onVehicleSelected(VehicleSelectedEvent e) {
		renderEventList();
	}

	private void onTrackerChanged(TrackerChangedEvent e) {
		renderEventList();
	}

	private void onOperationChanged(OperationChangedEvent e) {
		renderEventList();
	}

	private Component createToolbar() {
		Button addEventButton = new Button("Add event");
		addEventButton.addClickListener(click -> addEvent());

		var toolbar = new HorizontalLayout(addEventButton);
		toolbar.addClassName("toolbar");
		return toolbar;
	}

	private void createEventList() {
		eventList = new VerticalLayout();
		eventList.addClassName("event-list");
		eventList.setSizeFull();

		renderEventList();
	}

	public void renderEventList() {
		List<Tracker> trackers = service.findTrackers();
		List<Event> events = service.findEvents().reversed();

		eventList.removeAll();

		for (Event event : events)
			eventList.add(new EventItem(
					event,
					this::renderEventList,
					mainLayout::fireEventChangedEvent,
					mainLayout::fireOperationChangedEvent,
					trackers,
					service
			));

		eventList.addClassNames("event-item");
	}

	public void addEvent() {
		// TODO: disable add button if no vehicle is present
		if (service.createAndSaveEvent()) {
			renderEventList();
			mainLayout.fireEventChangedEvent();
		}
	}
}
