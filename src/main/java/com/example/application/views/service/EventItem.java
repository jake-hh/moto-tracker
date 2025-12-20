package com.example.application.views.service;

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
	private Runnable refreshEventList;
	private List<Tracker> trackers;
	private MainService service;
	private Optional<Integer> emptyPos;

	public EventItem(Event event, Runnable refreshEventList, List<Tracker> trackers, MainService service) {
		this.event = event;
		this.refreshEventList = refreshEventList;
		this.trackers = trackers;
		this.service = service;
		this.emptyPos = Optional.empty();

		this.setAlignItems(FlexComponent.Alignment.START);
		this.setPadding(true);
		this.setSpacing(true);
		this.addClassName("mt-list-item-border");

		render();
	}

	public void updateEvent(Consumer<Event> mutator) {
		Event fresh = service.findUpdatedEvent(event);
		mutator.accept(fresh);
		service.saveEvent(fresh);
		event = fresh;
		render();
	}

	public void render() {
		render(Optional.empty());
	}

	public void render(Optional<Integer> newOperationPos) {
		this.removeAll();
		this.emptyPos = newOperationPos;

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
			Event updatedEvent = service.findEventById(event.getId()).get();
			List<Operation> operations = service.findAllOperations(event);

			if (operations.isEmpty()) {
				service.deleteEvent(updatedEvent);
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
						service.deleteOperation(operation, false);

					service.deleteEvent(updatedEvent);
					refreshEventList.run();
				});

				dialog.open();
			}
		});

		return deleteButton;
	}

	private IntegerField createMileageField() {
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

		return mileageField;
	}

	private DatePicker createDateField() {
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

		return dateField;
	}

	private VerticalLayout createOperationList(Optional<Integer> newOperationPos) {
		var operationList = new VerticalLayout();
		operationList.setPadding(false);
		operationList.setSpacing(false);

		List<Operation> operations = getOperations(newOperationPos);
		Optional<OperationItem> prevItem = Optional.empty();

		for (Operation operation : operations) {
			var opItem = new OperationItem(this, trackers, service, operationList, operation);
			operationList.add(opItem);

			opItem.updateTrackerLabel();
			opItem.updateRemoveButton(operations.size());
			opItem.updateAddButton(opItem, prevItem);
			prevItem = Optional.of(opItem);
		}

		return operationList;
	}

	public List<Operation> getOperations(Optional<Integer> newOperationPos) {
		List<Operation> operations = service.findAllOperations(event);

		if (operations.isEmpty()) {
			var op = new Operation();
			op.setEvent(event);
			operations.add(op);
			emptyPos = Optional.of(0);
		}

		else newOperationPos.ifPresent(position -> {
			var op = new Operation();
			op.setEvent(event);
			operations.add(position, op);
			emptyPos = Optional.of(position);
		});

		return operations;
	}

	public Optional<Integer> getEmptyPos() {
		return emptyPos;
	}
}
