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

import jakarta.annotation.Nullable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;


@SuppressWarnings("FieldMayBeFinal")
public class EventItem extends HorizontalLayout {

	private EventItemController controller;
	private Runnable refreshEventList;

	public EventItem(Event event, Runnable refreshEventList, List<Tracker> trackers, MainService service) {
		this.controller = new EventItemController(service, event, trackers);
		this.refreshEventList = refreshEventList;

		this.setAlignItems(FlexComponent.Alignment.START);
		this.setPadding(true);
		this.setSpacing(true);
		this.addClassName("mt-list-item-border");

		render();
	}

	private void updateEvent(Consumer<Event> mutator) {
		Event fresh = controller.getService().findUpdatedEvent(controller.getEvent());
		mutator.accept(fresh);
		controller.getService().saveEvent(fresh);
		controller.setEvent(fresh);
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
			// Get event with updated version (event record has outdated version after saving changes in db)
			Event updatedEvent = controller.getService().findEventById(controller.getEvent().getId()).get();
			List<Operation> operations = controller.getService().findAllOperations(controller.getEvent());

			if (operations.isEmpty()) {
				controller.getService().deleteEvent(updatedEvent);
				refreshEventList.run();
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
						controller.getService().deleteOperation(operation, false);

					controller.getService().deleteEvent(updatedEvent);
					refreshEventList.run();
				});

				dialog.open();
			}
		});

		return deleteButton;
	}

	private IntegerField createMileageField() {
		var mileageField = new IntegerField("Mileage");
		mileageField.setValue(controller.getEvent().getMileage());
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

		return mileageField;
	}

	private DatePicker createDateField() {
		var dateField = new DatePicker("Date");
		Optional.ofNullable(controller.getEvent().getDate())
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

		return dateField;
	}

	private VerticalLayout createOperationList(Integer newOperationPos) {
		var operationList = new VerticalLayout();
		operationList.setPadding(false);
		operationList.setSpacing(false);

		OperationRender render = controller.getOperations(newOperationPos);
		List<Operation> operations = render.operations();
		Integer emptyPos = render.emptyPos();

		@Nullable
		OperationItem prevItem = null;

		for (Operation operation : operations) {
			var opItem = new OperationItem(this, controller.getTrackers(), controller.getService(), operationList, operation, emptyPos);
			operationList.add(opItem);

			opItem.updateTrackerLabel();
			opItem.updateRemoveButton(operations.size());
			opItem.updateAddButton(opItem, prevItem);
			prevItem = opItem;
		}

		return operationList;
	}
}
