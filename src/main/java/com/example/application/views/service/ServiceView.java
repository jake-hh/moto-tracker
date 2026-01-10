package com.example.application.views.service;

import com.example.application.data.Event;
import com.example.application.data.Tracker;
import com.example.application.events.VehicleSelectedEvent;
import com.example.application.services.MainService;
import com.example.application.services.UserSettingsService;
import com.example.application.views.MainLayout;

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
	private final MainService mainService;
	private final UserSettingsService settingsService;

	public ServiceView(
			MainService mainService,
			UserSettingsService settingsService,
			MainLayout layout
	) {
		this.mainService = mainService;
		this.settingsService = settingsService;
		layout.addVehicleSelectedListener(this::onVehicleSelected);

		addClassName("service-view");
		//setPadding(true);
		//setSpacing(true);
		setSizeFull();

		createEventList();
		add(getToolbar(), eventList);
	}

	private void onVehicleSelected(VehicleSelectedEvent e) {
		renderEventList();
	}

	private Component getToolbar() {
		Button addEventButton = new Button("Add event");
		addEventButton.addClickListener(click -> addEvent());

		var toolbar = new HorizontalLayout(addEventButton);
		toolbar.addClassName("service-toolbar");
		return toolbar;
	}

	private void createEventList() {
		eventList = new VerticalLayout();
		eventList.addClassName("service-event-list");
		eventList.setSizeFull();

		renderEventList();
	}

	public void renderEventList() {
		List<Tracker> trackers = settingsService.getSelectedVehicle()
				.map(mainService::findTrackersByVehicle)
				.orElse(List.of());

		List<Event> events = settingsService.getSelectedVehicle()
				.map(mainService::findEventsByVehicle)
				.map(List::reversed)
				.orElse(List.of());

		eventList.removeAll();

		for (Event event : events)
			eventList.add(new EventItem(event, this::renderEventList, trackers, mainService));

		eventList.addClassNames("service-event-item");
	}

	public void addEvent() {
		// TODO: disable add button if no vehicle is present
		settingsService.getSelectedVehicle()
				.ifPresent(vehicle -> {
					mainService.createAndSaveEventForVehicle(vehicle);
					renderEventList();
				});
	}
}
