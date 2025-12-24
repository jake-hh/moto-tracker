package com.example.application.views.service;

import com.example.application.data.Event;
import com.example.application.data.Operation;
import com.example.application.data.Tracker;
import com.example.application.services.MainService;
import com.example.application.views.service.EventItemController.OperationRender;

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


public class EventItem extends HorizontalLayout {

	private final EventItemController controller;
	private final Runnable refreshEventList;
	private final List<Tracker> trackers;

	public EventItem(Event event, Runnable refreshEventList, List<Tracker> trackers, MainService service) {
		this.controller = new EventItemController(service, event);
		this.refreshEventList = refreshEventList;
		this.trackers = trackers;

		this.setAlignItems(FlexComponent.Alignment.START);
		this.setPadding(true);
		this.setSpacing(true);
		this.addClassName("mt-list-item-border");

		render();
	}

	public void render() {
		render(null);
	}

	public void render(Integer newOperationPos) {
		this.removeAll();

		add(createDeleteButton(),
			createMileageField(),
			createDateField(),
			createOperationList(newOperationPos));
	}

	private Button createDeleteButton() {
		var deleteButton = new Button(new Icon(VaadinIcon.TRASH));
		deleteButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
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
				controller.updateEvent(e -> e.setMileage(mileage), this::render);
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
				controller.updateEvent(e -> e.setDate(date), this::render);
			}
		});

		return dateField;
	}

	private VerticalLayout createOperationList(Integer newOperationPos) {
		var operationList = new VerticalLayout();
		operationList.setPadding(false);
		operationList.setSpacing(false);

		OperationRender r = controller.prepareOperations(newOperationPos);

		for (int i = 0; i < r.operations().size(); i++) {

			Operation operation = r.operations().get(i);
			var nextPos = r.nextInsertPos(i);

			var opItem = new OperationItem(operation, trackers);

			operationList.add(opItem);

			opItem.enableTrackerLabel(i == 0);
			opItem.disableRemoveButton(r.cannotRemove(i));
			opItem.disableAddButton(r.isEmpty(i));
			opItem.disableAddButton(r.isEmpty(i + 1));

			opItem.onAddButtonPressed(() -> {
					// Add new operation to logic list but don't save it in db, it will be saved when user sets the tracker
					render(nextPos);
			});

			opItem.onRemoveButtonPressed(() -> {
					controller.deleteOperation(operation);
					render();
			});

			opItem.onTrackerBoxChanged(tracker -> {
					controller.updateOperation(operation, tracker);
					render();
			});
		}

		return operationList;
	}
}
