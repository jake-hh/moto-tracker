package com.example.application.views.service;

import com.example.application.data.Event;
import com.example.application.data.Operation;
import com.example.application.data.Tracker;
import com.example.application.services.MainService;
import com.example.application.views.MainLayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;

import jakarta.annotation.security.PermitAll;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.stream.Stream;


@SpringComponent
@Scope("prototype")
@PermitAll
@Route(value = "services", layout = MainLayout.class)
@PageTitle("Services | Moto Tracker")
public class ServiceView extends VerticalLayout {
	// TextField filterText = new TextField();
	VerticalLayout eventList;
	List<Tracker> trackers;
	MainService service;

	public ServiceView(MainService service) {
		this.service = service;
		addClassName("service-view");
		//setPadding(true);
		//setSpacing(true);
		setSizeFull();
		updateTrackers();

		eventList = getEventList();
		add(getToolbar(), eventList);
		//updateList();
	}

	private void updateTrackers() {
		trackers = service.findAllTrackers();
	}

	private Component getToolbar() {
		// filterText.setPlaceholder("Filter by name...");
		// filterText.setClearButtonVisible(true);
		// filterText.setValueChangeMode(ValueChangeMode.LAZY);
		// filterText.addValueChangeListener(e -> updateList());

		Button addEventButton = new Button("Add event");
		//addEventButton.addClickListener(click -> addEvent());

		var toolbar = new HorizontalLayout(/*filterText, */addEventButton);
		toolbar.addClassName("service-toolbar");
		return toolbar;
	}

	private VerticalLayout getEventList() {
		var eventList = new VerticalLayout();
		eventList.addClassName("service-event-list");
		eventList.setSizeFull();
		//eventList.removeAll();

		for (Event event : service.findAllEvents().reversed()) {
			eventList.add(getEventItem(event));
		}

		eventList.addClassNames("service-event-item");
		return eventList;
	}

	private HorizontalLayout getEventItem(Event event) {
		var eventItem = new HorizontalLayout();
		eventItem.setAlignItems(Alignment.START);
		eventItem.setPadding(true);
		eventItem.setSpacing(true);
		eventItem.getStyle().set("border", "1px solid var(--lumo-contrast-20pct)");
		eventItem.getStyle().set("border-radius", "8px");

		// Event item fields
		var deleteButton = new Button(new Icon(VaadinIcon.TRASH));
		deleteButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
		deleteButton.getElement().setAttribute("title", "Remove this event");

		deleteButton.addClickListener(e -> {
			// Get event with updated version (event record has outdated version after saving changes in db)
			service.deleteEventById(event.getId());
			eventList.remove(eventItem);
		});

		var mileageField = new IntegerField("Mileage");
		mileageField.setValue(event.getMileage());
		mileageField.addValueChangeListener(mileageEv -> {
			Event freshEvent = service.findUpdatedEvent(event);
			freshEvent.setMileage(mileageEv.getValue());
			service.saveEvent(freshEvent);
		});

		var dateField = new TextField("Date");
		dateField.setValue(event.getDate());
		dateField.addValueChangeListener(dateEv -> {
			Event freshEvent = service.findUpdatedEvent(event);
			freshEvent.setDate(dateEv.getValue());
			service.saveEvent(freshEvent);
		});

		eventItem.add(deleteButton, dateField, mileageField, getOperationList(event));
		return eventItem;
	}

	private VerticalLayout getOperationList(Event event) {
		var operationList = new VerticalLayout();
		operationList.setPadding(false);
		operationList.setSpacing(false);

		for (Operation operation : service.findAllOperations(event)) {
			operationList.add(getOperationItem(operationList, operation, event, true));
		}

		addDefaultOperationIfEmpty(operationList, event);
		//updateRemoveButtonsState(operationList);

		return operationList;
	}

	private HorizontalLayout getOperationItem(VerticalLayout operationList, Operation operation, Event event, Boolean enableRemoveButton) {
		var operationItem = new HorizontalLayout();
		operationItem.setAlignItems(Alignment.END);
		operationItem.setSpacing(true);

		// --- Tracker ComboBox ---
		var trackerBox = new ComboBox<Tracker>("Tracker");
		trackerBox.setItems(trackers);
		trackerBox.setItemLabelGenerator(Tracker::getName);
		trackerBox.setValue(operation.getTracker());

		// --- Menubar buttons ---
		var menuBar = new HorizontalLayout();
		menuBar.setSpacing(false);
		menuBar.setPadding(false);

		var addButton = new Button(new Icon(VaadinIcon.PLUS));
		addButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
		addButton.getElement().setAttribute("title", "Add new operation");

		var removeButton = new Button(new Icon(VaadinIcon.TRASH));
		removeButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
		removeButton.getElement().setAttribute("title", "Remove this operation");
		removeButton.setEnabled(enableRemoveButton);

		trackerBox.addValueChangeListener(trackerEv -> {
			// Get operation with updated version (operation has outdated version after saving in db trackerBox change event)
			Operation freshOp = service.findUpdatedOperation(operation);
			freshOp.setTracker(trackerEv.getValue());
			service.saveOperation(freshOp);

			removeButton.setEnabled(true);
		});

		// --- Button logic ---
		addButton.addClickListener(e -> {
			// Add new operation to GUI list but don't save it in db, it will be saved when user sets the tracker
			createOperationItem(operationList, event, true);

			setRemoveButtonsState(operationList, true);
		});

		removeButton.addClickListener(e -> {
			// Get operation with updated version (operation has outdated version after saving in db trackerBox change event)
			service.deleteOperationById(operation.getId());
			operationList.remove(operationItem);
			addDefaultOperationIfEmpty(operationList, event);
		});

		menuBar.add(addButton, removeButton);
		operationItem.add(trackerBox, menuBar);

		return operationItem;
	}

	private void addDefaultOperationIfEmpty(VerticalLayout operationList, Event event) {
		if (operationList.getComponentCount() == 0) {
			createOperationItem(operationList, event, false);
		}
	}

	private void createOperationItem(VerticalLayout operationList, Event event, Boolean enableRemoveButton) {
		var op = new Operation();
		op.setEvent(event);
		operationList.add(getOperationItem(operationList, op, event, enableRemoveButton));
	}

	//private void updateRemoveButtonsState(VerticalLayout operationList) {
		//int count = operationList.getComponentCount();
		////System.out.println("operationList size: " + count);

		//setRemoveButtonsState(operationList, count > 1);
	//}

	private void setRemoveButtonsState(VerticalLayout operationList, Boolean state) {
		// Disable remove button if only one item is in list
		operationList.getChildren().forEach(component -> {
			if (component instanceof HorizontalLayout hl) {
				hl.getChildren()
					.flatMap(c -> c instanceof HorizontalLayout hl2 ? hl2.getChildren() : Stream.of(c))
					.filter(Button.class::isInstance)
					.map(Button.class::cast)
					.filter(btn -> btn.getElement().getAttribute("title").equals("Remove this operation"))
					.forEach(btn -> btn.setEnabled(state));
			}
		});
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
	}

	private void addEvent() {
		//grid.asSingleSelect().clear();
		//editOperation(new Operation());
	}

	private void updateList() {
		grid.setItems(service.findAllOperations(/*filterText.getValue()*));
	}
	*/
}
