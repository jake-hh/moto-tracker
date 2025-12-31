package com.example.application.views.service;

import com.example.application.data.Event;
import com.example.application.data.Operation;
import com.example.application.data.Tracker;
import com.example.application.services.MainService;
import com.example.application.views.service.OperationRowsBuilder.*;

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
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;


public class EventItem extends HorizontalLayout {

	private final EventItemController controller;
	private final Runnable refreshEventList;
	private final List<Tracker> allTrackers;
	private final VerticalLayout operationList;

	public EventItem(Event event, Runnable refreshEventList, List<Tracker> allTrackers, MainService service) {
		this.controller = new EventItemController(service, event);
		this.refreshEventList = refreshEventList;
		this.allTrackers = allTrackers;

		setAlignItems(FlexComponent.Alignment.START);
		setPadding(true);
		setSpacing(true);
		addClassName("mt-list-item-border");

		operationList = new VerticalLayout();
		operationList.setPadding(false);
		operationList.setSpacing(false);

		refreshAll();
	}

	public void refreshAll() {
		removeAll();

		add(createDeleteButton(),
			createMileageField(),
			createDateField(),
			operationList);

		refreshOperationList();
	}

	private Button createDeleteButton() {
		var deleteButton = new Button(new Icon(VaadinIcon.TRASH));

		deleteButton.addThemeVariants(
				ButtonVariant.LUMO_ICON,
				ButtonVariant.LUMO_ERROR,
				ButtonVariant.LUMO_TERTIARY
		);
		deleteButton.getElement().setAttribute("title", "Remove this event");

		deleteButton.addClickListener(e -> {
			int operationsCount = controller.getOperationCount();

			if (operationsCount == 0) {
				controller.deleteEvent();
				refreshEventList.run();
			}
			else {
				openDeleteDialog(operationsCount);
			}
		});

		return deleteButton;
	}

	private void openDeleteDialog(int operationsCount) {
		var dialog = new ConfirmDialog();
		if (operationsCount == 1) {
			dialog.setHeader("Event contains one operation");
			dialog.setText("Do you want to delete it?");
		} else {
			dialog.setHeader("Event contains " + operationsCount + " operations");
			dialog.setText("Do you want to delete them all?");
		}
		dialog.setCancelable(true);
		dialog.setConfirmText("Delete");
		dialog.setConfirmButtonTheme("error primary");

		dialog.addConfirmListener(ce -> {
			controller.deleteEventWithOperations();
			refreshEventList.run();
		});

		dialog.open();
	}

	private IntegerField createMileageField() {
		var mileageField = new IntegerField("Mileage");
		controller.getMileage().ifPresent(mileageField::setValue);

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
				controller.updateMileage(mileage);
		});

		return mileageField;
	}

	private DatePicker createDateField() {
		var dateField = new DatePicker("Date");
		controller.getDate().ifPresent(dateField::setValue);

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

			if (date != null && !date.isAfter(ServiceView.getDateToday()))
				controller.updateDate(date);
		});

		return dateField;
	}

	public void refreshOperationList() {
		refreshOperationListAndAdd(null);
	}

	private List<Tracker> getAvailableTrackers(List<Operation> operations) {
		var usedTrackers = new HashSet<Tracker>();

		for (Operation op : operations)
			usedTrackers.add(op.getTracker());

		return allTrackers.stream()
				.filter(t -> !usedTrackers.contains(t))
				.toList();
	}

	private void refreshOperationListAndAdd(Integer newOperationPos) {
		List<Operation> operations = controller.getOperations();
		List<OperationRow> rows = OperationRowsBuilder.build(operations, newOperationPos);
		List<Tracker> availableTrackers = getAvailableTrackers(operations);

		operationList.removeAll();

		for (OperationRow row : rows)
			operationList.add(createOperationItem(row, availableTrackers));
	}

	@NotNull
	private OperationItem createOperationItem(OperationRow row, List<Tracker> availableTrackers) {
		final OperationItem item;

		if (row instanceof ExistingOperationRow existing) {
			Operation op = existing.operation();
			item = new OperationItem(op.getTracker(), availableTrackers);

			item.onTrackerBoxChanged(tracker -> {
				controller.updateOperation(op, tracker);
				refreshOperationList();
			});

			if (row.canRemove())
				item.onRemoveButtonPressed(() -> {
					controller.deleteOperation(op);
					refreshOperationList();
				});
			else
				item.disableRemoveButton(true);
		}
		else if (row instanceof NewOperationRow) {
			item = new OperationItem(null, availableTrackers);

			item.onTrackerBoxChanged(tracker -> {
				controller.createOperation(tracker);
				refreshOperationList();
			});

			if (row.canRemove())
				item.onRemoveButtonPressed(this::refreshOperationList);
			else
				item.disableRemoveButton(true);
		}
		else throw new IllegalStateException("UnknownOperationRow type");

		item.enableTrackerLabel(row.hasLabel());

		if (row.canAdd())
			item.onAddButtonPressed(() -> refreshOperationListAndAdd(row.nextPos()));
		else
			item.disableAddButton(true);

		return item;
	}
}
