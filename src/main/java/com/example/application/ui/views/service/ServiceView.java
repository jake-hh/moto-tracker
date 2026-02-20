package com.example.application.ui.views.service;

import com.example.application.data.entity.Event;
import com.example.application.data.entity.Tracker;
import com.example.application.services.MainService;
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

		mainLayout.addVehicleSelectedListener(e -> updateEventList());
		mainLayout.addTrackerChangedListener(e -> updateEventList());
		mainLayout.addOperationChangedListener(e -> updateEventList());

		addClassName("view");
		//setPadding(true);
		//setSpacing(true);
		setSizeFull();

		createEventList();
		add(createToolbar(), eventList);
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

		updateEventList();
	}

	public void updateEventList() {
		List<Tracker> trackers = service.findTrackers();
		List<Event> events = service.findEvents().reversed();

		eventList.removeAll();

		for (Event event : events)
			eventList.add(new EventItem(
					event,
					this::updateEventList,
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
			updateEventList();
			mainLayout.fireEventChangedEvent();
		}
	}
}
