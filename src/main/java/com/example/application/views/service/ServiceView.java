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
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;

import jakarta.annotation.security.PermitAll;
import org.springframework.context.annotation.Scope;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


@SuppressWarnings("FieldMayBeFinal")
@SpringComponent
@Scope("prototype")
@PermitAll
@Route(value = "services", layout = MainLayout.class)
@PageTitle("Services | Moto Tracker")
public class ServiceView extends VerticalLayout {
	private VerticalLayout eventList;
	private List<Tracker> trackers;
	private MainService service;

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
		Button addEventButton = new Button("Add event");
		addEventButton.addClickListener(click -> addEvent());

		var toolbar = new HorizontalLayout(addEventButton);
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
			Event updatedEvent = service.findUpdatedEvent(event);
			updatedEvent.setMileage(mileageEv.getValue());
			service.saveEvent(updatedEvent);
		});

		var dateField = new TextField("Date");
		Optional.ofNullable(event.getDate())
				.ifPresent(dateField::setValue);
		dateField.addValueChangeListener(dateEv -> {
			Event updatedEvent = service.findUpdatedEvent(event);
			updatedEvent.setDate(dateEv.getValue());
			service.saveEvent(updatedEvent);
		});

		eventItem.add(deleteButton, dateField, mileageField, getOperationList(event));
		return eventItem;
	}

	private VerticalLayout getOperationList(Event event) {
		var operationList = new VerticalLayout();
		operationList.setPadding(false);
		operationList.setSpacing(false);

		for (Operation operation : service.findAllOperations(event)) {
			operationList.add(new OperationItem(operationList, operation, event));
		}

		addDefaultOperationIfEmpty(operationList, event);
		//updateRemoveButtonsState(operationList);

		return operationList;
	}

	private void addDefaultOperationIfEmpty(VerticalLayout operationList, Event event) {
		if (operationList.getComponentCount() == 0)
			createOperationItem(operationList, event, false);
	}

	private void createOperationItem(VerticalLayout operationList, Event event, boolean enableRemoveButton) {
		var op = new Operation();
		op.setEvent(event);

		var operationItem = new OperationItem(operationList, op, event);
		operationItem.setRemoveButtonEnabled(enableRemoveButton);
		operationList.add(operationItem);
	}

	//private void updateRemoveButtonsState(VerticalLayout operationList) {
		//int count = operationList.getComponentCount();
		////System.out.println("operationList size: " + count);

		//// Disable remove button if only one item is in list
		//setRemoveButtonsState(operationList, count > 1);
	//}

	private Stream<OperationItem> getChildrenStream(VerticalLayout operationList) {
		return operationList.getChildren()
				.filter(OperationItem.class::isInstance)
				.map(OperationItem.class::cast);
	}

	private void setRemoveButtonsState(VerticalLayout operationList, boolean state) {
		getChildrenStream(operationList).forEach(item -> item.setRemoveButtonEnabled(state));
	}

	private void addEvent() {
		var event = new Event();
		event.setDate(LocalDate.now().toString());

		service.saveEvent(event);
		eventList.addComponentAsFirst(getEventItem(event));
	}

	class OperationItem extends HorizontalLayout {
		private ComboBox<Tracker> trackerBox = new ComboBox<>("Tracker");
		private HorizontalLayout menuBar = new HorizontalLayout();
		private Button addButton = new Button(new Icon(VaadinIcon.PLUS));
		private Button removeButton = new Button(new Icon(VaadinIcon.TRASH));

		private OperationItem(VerticalLayout operationList, Operation operation, Event event) {
			this.setAlignItems(Alignment.END);
			this.setSpacing(true);

			// --- Tracker ComboBox ---
			trackerBox.setItems(trackers);
			trackerBox.setItemLabelGenerator(Tracker::getName);
			trackerBox.setValue(operation.getTracker());

			// --- Menubar buttons ---
			menuBar.setSpacing(false);
			menuBar.setPadding(false);

			addButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
			addButton.getElement().setAttribute("title", "Add new operation");

			removeButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
			removeButton.getElement().setAttribute("title", "Remove this operation");

			// --- Listeners ---
			trackerBox.addValueChangeListener(trackerEv -> {
				// Get operation with updated version (operation has outdated version after saving in db trackerBox change event)
				Operation updatedOperation = service.findUpdatedOperation(operation);
				updatedOperation.setTracker(trackerEv.getValue());
				service.saveOperation(updatedOperation);

				removeButton.setEnabled(true);
			});

			addButton.addClickListener(e -> {
				// Add new operation to GUI list but don't save it in db, it will be saved when user sets the tracker
				createOperationItem(operationList, event, true);

				setRemoveButtonsState(operationList, true);
			});

			removeButton.addClickListener(e -> {
				// Get operation with updated version (operation has outdated version after saving in db trackerBox change event)
				service.deleteOperationById(operation.getId());
				operationList.remove(this);
				addDefaultOperationIfEmpty(operationList, event);

				// If last remaining item is empty -> disable its remove button
				var remainingItems = getChildrenStream(operationList).toList();
				if (remainingItems.size() == 1)
					remainingItems.getFirst().updateRemoveButtonState();
			});

			menuBar.add(addButton, removeButton);
			this.add(trackerBox, menuBar);
		}

		// Call only if it's the last item in list
		private void updateRemoveButtonState() {
			boolean isEmpty = trackerBox.getValue() == null;
			setRemoveButtonEnabled(!isEmpty); // disable if empty
		}

		private void setRemoveButtonEnabled(boolean state) {
			removeButton.setEnabled(state);
		}
	}
}
