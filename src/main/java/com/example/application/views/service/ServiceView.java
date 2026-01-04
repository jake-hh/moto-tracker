package com.example.application.views.service;

import com.example.application.data.Event;
import com.example.application.data.Tracker;
import com.example.application.event.SelectedVehicleEvent;
import com.example.application.services.MainService;
import com.example.application.views.MainLayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import jakarta.annotation.security.PermitAll;
import org.springframework.context.event.EventListener;

import java.util.List;


@SpringComponent
@UIScope
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
		List<Tracker> trackers = service.findTrackers();

		eventList.removeAll();

		for (Event event : service.findEvents().reversed()) {
			eventList.add(new EventItem(event, this::renderEventList, trackers, service));
		}

		eventList.addClassNames("service-event-item");
	}

	public void addEvent() {
		service.createAndSaveEvent();
		renderEventList();
	}

	@EventListener
	public void onSelectedVehicle(SelectedVehicleEvent e) {
		UI ui = UI.getCurrent();
		if (ui == null) // event from another UI/session
			return;

		ui.access(this::renderEventList);
	}
}
