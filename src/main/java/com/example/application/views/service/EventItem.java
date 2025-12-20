package com.example.application.views.service;

import com.example.application.Notify;
import com.example.application.data.Event;
import com.example.application.data.Operation;
import com.example.application.data.Tracker;
import com.example.application.services.MainService;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;


@SuppressWarnings("FieldMayBeFinal")
public class EventItem extends HorizontalLayout {
	private Event event;
	private VerticalLayout eventList;
	private List<Tracker> trackers;
	private MainService service;

	public EventItem(Event event, VerticalLayout eventList, List<Tracker> trackers, MainService service) {
		this.event = event;
		this.eventList = eventList;
		this.trackers = trackers;
		this.service = service;

		this.setAlignItems(FlexComponent.Alignment.START);
		this.setPadding(true);
		this.setSpacing(true);
		this.addClassName("mt-list-item-border");

		renderContent();
	}

	private VerticalLayout getOperationList(Event event) {
		var operationList = new VerticalLayout();
		operationList.setPadding(false);
		operationList.setSpacing(false);

		for (Operation operation : getOperations()) {
			var opItem = new OperationItem(this, trackers, service, operationList, operation, event);
			operationList.add(opItem);
			opItem.updateTrackerLabel();
		}

		return operationList;
	}

	public void updateEvent(Consumer<Event> mutator) {
		Event fresh = service.findUpdatedEvent(event);
		mutator.accept(fresh);
		service.saveEvent(fresh);
		event = fresh;
		renderContent();
	}

	//public void updateOperation(Consumer<Operation> mutator) {
		//Operation op = service.findUp
	//}

	public void renderContent() {
		this.removeAll();

		var deleteButton = new Button(new Icon(VaadinIcon.TRASH));
		deleteButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
		deleteButton.getElement().setAttribute("title", "Remove this event");

		deleteButton.addClickListener(e -> {
			// Get event with updated version (event record has outdated version after saving changes in db)
			Event updatedEvent = service.findEventById(event.getId()).get();
			List<Operation> operations = service.findAllOperations(event);

			if (operations.isEmpty()) {
				service.deleteEvent(updatedEvent);
				eventList.remove(this);
			}
			else {
				var dialog = new ConfirmDialog();
				if (operations.size() == 1) {
					dialog.setHeader("Event contains one operation");
					dialog.setText("Do you want to delete it?");
				} else {
					dialog.setHeader("Event contains " + operations.size() + " operations");
					dialog.setText("Do you want to delete them all?");
				}
				dialog.setCancelable(true);
				dialog.setConfirmText("Delete");
				dialog.setConfirmButtonTheme("error primary");

				dialog.addConfirmListener(ce -> {
					for (Operation operation : operations)
						service.deleteOperation(operation, false);

					service.deleteEvent(updatedEvent);
					eventList.remove(this);
				});

				dialog.open();
			}
		});

		var mileageField = new IntegerField("Mileage");
		mileageField.setValue(event.getMileage());
		mileageField.setStepButtonsVisible(true);
		mileageField.setStep(100);
		mileageField.setSuffixComponent(new Span("km"));
		//mileageField.setHelperText("km");

		mileageField.setI18n(new IntegerField.IntegerFieldI18n()
				.setBadInputErrorMessage("Invalid number format")
				.setStepErrorMessage("Number must be a multiple of 100"));

		mileageField.addValueChangeListener(mileageEv -> {
			Integer mileage = mileageEv.getValue();

			if (mileage == null || mileage % 100 == 0)
				updateEvent(e -> e.setMileage(mileage));
		});

		var dateField = new DatePicker("Date");
		Optional.ofNullable(event.getDate())
				.ifPresent(dateField::setValue);

		dateField.setRequired(true);
		dateField.setRequiredIndicatorVisible(false);
		dateField.setMax(ServiceView.getDateToday());
		dateField.setPlaceholder("yyyy-MM-dd");
		//dateField.setHelperText("yyyy-MM-dd");

		dateField.setI18n(new DatePicker.DatePickerI18n()
				.setFirstDayOfWeek(1)
				.setDateFormat("yyyy-MM-dd")
				.setRequiredErrorMessage("Field is required")
				.setBadInputErrorMessage("Invalid date format")
				.setMaxErrorMessage("Future dates aren’t allowed"));

		dateField.addValueChangeListener(dateEv -> {
			LocalDate date = dateEv.getValue();

			if (date != null && !date.isAfter(ServiceView.getDateToday())) {
				updateEvent(e -> e.setDate(date));
			}
		});

		this.add(deleteButton, dateField, mileageField, getOperationList(event));
	}

	public List<Operation> getOperations() {
		List<Operation> operations = service.findAllOperations(event);

		if (operations.isEmpty()) {
			var op = new Operation();
			op.setEvent(event);
			operations.add(op);
			Notify.ok("Added new Operation: " + op + " for Event: " + event);
		}

		return operations;
	}

	/*
	public void addNewOperationItem(VerticalLayout operationList, Event event, int position, boolean enableAddButton, boolean enableRemoveButton) {
		var op = new Operation();
		op.setEvent(event);

		var operationItem = new OperationItem(this, trackers, service, operationList, op, event);
		operationItem.setAddButtonEnabled(enableAddButton);
		operationItem.setRemoveButtonEnabled(enableRemoveButton);
		operationList.addComponentAtIndex(position, operationItem);
		operationItem.updateTrackerLabel();
	}

	public Stream<OperationItem> getChildrenStream(VerticalLayout operationList) {
		return operationList.getChildren()
				.filter(OperationItem.class::isInstance)
				.map(OperationItem.class::cast);
	}

	public void enableAllAddButtons(VerticalLayout operationList) {
		getChildrenStream(operationList).forEach(item -> item.setAddButtonEnabled(true));
	}

	public void enableAllRemoveButtons(VerticalLayout operationList) {
		getChildrenStream(operationList).forEach(item -> item.setRemoveButtonEnabled(true));
	}

	public void deleteEmptyOperationItems(VerticalLayout operationList, int omitPosition) {
		getChildrenStream(operationList).forEach(item -> {
			if (item.isEmpty() && operationList.indexOf(item) != omitPosition)
				operationList.remove(item);
		});
	}

	public void updateAddButtonStates(VerticalLayout operationList) {
		enableAllAddButtons(operationList);

		getChildrenStream(operationList).forEach(item -> {
			if (item.isEmpty()) {
				item.setAddButtonEnabled(false);

				var prevPos = operationList.indexOf(item) - 1;
				if (prevPos >= 0 && operationList.getComponentAt(prevPos) instanceof OperationItem prevItem)
					prevItem.setAddButtonEnabled(false);
			}
		});
	}*/
}
