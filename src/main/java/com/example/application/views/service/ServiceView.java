package com.example.application.views.service;

import com.example.application.data.Event;
import com.example.application.data.Operation;
import com.example.application.data.Tracker;
import com.example.application.services.MainService;
import com.example.application.views.MainLayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.component.textfield.TextField;
//import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;

import jakarta.annotation.security.PermitAll;
import org.springframework.context.annotation.Scope;

import java.util.List;


@SpringComponent
@Scope("prototype")
@PermitAll
@Route(value = "services", layout = MainLayout.class)
@PageTitle("Services | Moto Tracker")
public class ServiceView extends VerticalLayout {
	// TextField filterText = new TextField();
	MainService service;

	public ServiceView(MainService service) {
		this.service = service;
		addClassName("service-view");
		//setPadding(true);
		//setSpacing(true);
		setSizeFull();

		add(getToolbar(), getEventList());
		//updateList();
	}

	private VerticalLayout getEventList() {
		var eventList = new VerticalLayout();
		eventList.addClassName("service-event-list");
		eventList.setSizeFull();
		//eventList.removeAll();

		List<Event> events = service.findAllEvents();

		for (Event event : events) {
			var eventItem = new HorizontalLayout();
			eventItem.setWidthFull();
			eventItem.setAlignItems(Alignment.CENTER);
			//setPadding(true);
			//setSpacing(true);
			//eventItem.getStyle().set("border", "1px solid var(--lumo-contrast-20pct)");

			// Event item fields
			var mileageField = new IntegerField("Mileage");
			mileageField.setValue(event.getMileage());
			mileageField.addValueChangeListener(e -> {
				event.setMileage(e.getValue());
				service.saveEvent(event);
			});

			var dateField = new TextField("Date");
			dateField.setValue(event.getDate());
			dateField.addValueChangeListener(e -> {
				event.setDate(e.getValue());
				service.saveEvent(event);
			});

			// Operations list
			VerticalLayout operationList = new VerticalLayout();
			operationList.setPadding(false);
			operationList.setSpacing(false);

			List<Operation> operations = service.findAllOperations(event);
			List<Tracker> trackers = service.findAllTrackers();

			for (Operation operation : operations) {
				ComboBox<Tracker> trackerBox = new ComboBox<>("Tracker");
				trackerBox.setItems(trackers);
				trackerBox.setItemLabelGenerator(Tracker::getName);
				trackerBox.setValue(operation.getTracker());

				trackerBox.addValueChangeListener(ev -> {
					operation.setTracker(ev.getValue());
					service.saveOperation(operation);
				});

				operationList.add(trackerBox);
			}

			eventItem.add(dateField, mileageField, operationList);
			eventList.add(eventItem);
		}

		eventList.addClassNames("service-event-item");
		return eventList;
	}

	/*
	private void saveOperation(ServiceForm.SaveEvent event) {
		service.saveOperation(event.getOperation());
		updateList();
		closeEditor();
	}

	private void deleteOperation(ServiceForm.DeleteEvent event) {
		service.deleteOperation(event.getOperation());
		updateList();
		closeEditor();
	}

	private void configureGrid() {
		grid.addClassNames("service-grid");
		grid.setSizeFull();
		// grid.setColumns("firstName", "lastName", "email");
		grid.addColumn(operation -> operation.getEvent().getDateStr()).setHeader("Date");
		grid.addColumn(operation -> operation.getEvent().getMileage()).setHeader("Mileage");
		grid.addColumn(operation -> operation.getTracker().getName()).setHeader("Tracker");
		grid.addColumn(operation -> operation.getTracker().getInterval()).setHeader("Interval");
		grid.addColumn(operation -> operation.getTracker().getRange()).setHeader("Range");
		grid.getColumns().forEach(col -> col.setAutoWidth(true));

		grid.asSingleSelect().addValueChangeListener(event ->
				editOperation(event.getValue()));
	}*/

	private Component getToolbar() {
		// filterText.setPlaceholder("Filter by name...");
		// filterText.setClearButtonVisible(true);
		// filterText.setValueChangeMode(ValueChangeMode.LAZY);
		// filterText.addValueChangeListener(e -> updateList());

		Button addEventButton = new Button("Add operation");
		//addEventButton.addClickListener(click -> addEvent());

		var toolbar = new HorizontalLayout(/*filterText, */addEventButton);
		toolbar.addClassName("service-toolbar");
		return toolbar;
	}

	/*
	private void addEvent() {
		//grid.asSingleSelect().clear();
		//editOperation(new Operation());
	}

	private void updateList() {
		grid.setItems(service.findAllOperations(/*filterText.getValue()*));
	}
	*/
}
