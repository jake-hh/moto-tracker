package com.example.application.views.service;

import com.example.application.data.Event;
import com.example.application.data.Tracker;
import com.example.application.services.MainService;
import com.example.application.views.MainLayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;

import jakarta.annotation.security.PermitAll;
import org.springframework.context.annotation.Scope;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;


@SpringComponent
@Scope("prototype")
@PermitAll
@Route(value = "services", layout = MainLayout.class)
@PageTitle("Services | Moto Tracker")
public class ServiceView extends VerticalLayout {

	private VerticalLayout eventList;
	private final MainService service;

	public ServiceView(MainService service) {
		this.service = service;
		addClassName("service-view");
		//setPadding(true);
		//setSpacing(true);
		setSizeFull();

		createEventList();
		add(getToolbar(), eventList);
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
		List<Tracker> trackers = service.findAllTrackers();

		eventList.removeAll();

		for (Event event : service.findAllEvents().reversed()) {
			eventList.add(new EventItem(event, this::renderEventList, trackers, service));
		}

		eventList.addClassNames("service-event-item");
	}

	public void addEvent() {
		var event = new Event();
		event.setDate(getDateToday());

		service.saveEvent(event);
		renderEventList();
	}

	public static LocalDate getDateToday() {
		return LocalDate.now(ZoneId.systemDefault());
	}
}
