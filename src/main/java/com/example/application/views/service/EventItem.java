package com.example.application.views.service;

import com.example.application.data.Event;
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
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;


public class EventItem extends HorizontalLayout {

	private final EventItemController controller;
	private final Runnable refreshEventList;
	private final List<Tracker> trackers;
	private final VerticalLayout operationList;

	public EventItem(Event event, Runnable refreshEventList, List<Tracker> trackers, MainService service) {
		this.controller = new EventItemController(service, event);
		this.refreshEventList = refreshEventList;
		this.trackers = trackers;

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
		controller.initMileageField(mileageField::setValue);
		mileageField.setStepButtonsVisible(true);
		mileageField.setStep(100);
		mileageField.setSuffixComponent(new Span("km"));
		//mileageField.setHelperText("km");

		mileageField.setI18n(new IntegerField.IntegerFieldI18n()
				.setBadInputErrorMessage("Invalid number format")
				.setStepErrorMessage("Number must be a multiple of 100"));

		mileageField.addValueChangeListener(mileageEv -> {
			Integer mileage = mileageEv.getValue();

			if (mileage == null || mileage % 100 == 0) {
				controller.updateEvent(e -> e.setMileage(mileage), this::refreshAll);
			}
		});

		return mileageField;
	}

	private DatePicker createDateField() {
		var dateField = new DatePicker("Date");
		controller.initDateField(dateField::setValue);

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
				controller.updateEvent(e -> e.setDate(date), this::refreshAll);
			}
		});

		return dateField;
	}

	public void refreshOperationList() {
		refreshOperationListAndAdd(null);
	}

	private void refreshOperationListAndAdd(Integer newOperationPos) {
		operationList.removeAll();

		List<OperationRow> rows = OperationRowsBuilder.build(controller.getOperations(), newOperationPos);

		for (OperationRow row : rows)
			operationList.add(createOperationItem(row));
	}

	@NotNull
	private OperationItem createOperationItem(OperationRow row) {
		final OperationItem item;

		if (row instanceof ExistingOperationRow existing) {
			item = new OperationItem(existing.operation(), trackers);

			item.onTrackerBoxChanged(tracker -> {
				controller.updateOperation(existing.operation(), tracker);
				refreshOperationList();
			});

			if (row.canRemove())
				item.onRemoveButtonPressed(() -> {
					controller.deleteOperation(existing.operation());
					refreshOperationList();
				});
			else
				item.disableRemoveButton(true);
		}
		else if (row instanceof NewOperationRow) {
			item = new OperationItem(null, trackers);

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
