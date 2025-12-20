package com.example.application.views.service;

import com.example.application.data.Event;
import com.example.application.data.Operation;
import com.example.application.data.Tracker;
import com.example.application.services.MainService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


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

		// Event item fields
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
			if (mileageEv.getValue() == null || mileageEv.getValue() % 100 == 0) {
				Event updatedEvent = service.findUpdatedEvent(event);
				updatedEvent.setMileage(mileageEv.getValue());
				service.saveEvent(updatedEvent);
			}
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
			if (dateEv.getValue() != null && !dateEv.getValue().isAfter(ServiceView.getDateToday())) {
				Event updatedEvent = service.findUpdatedEvent(event);
				updatedEvent.setDate(dateEv.getValue());
				service.saveEvent(updatedEvent);
			}
		});

		this.add(deleteButton, dateField, mileageField, getOperationList(event));
	}

	private VerticalLayout getOperationList(Event event) {
		var operationList = new VerticalLayout();
		operationList.setPadding(false);
		operationList.setSpacing(false);

		for (Operation operation : service.findAllOperations(event)) {
			operationList.add(new OperationItem(operationList, operation, event));
		}

		addNewOperationIfEmpty(operationList, event);

		updateAllTrackerLabels(operationList);

		return operationList;
	}

	private void addNewOperationIfEmpty(VerticalLayout operationList, Event event) {
		if (operationList.getComponentCount() == 0)
			addNewOperationItem(operationList, event, 0, true, false);
	}

	private void addNewOperationItem(VerticalLayout operationList, Event event, int position, boolean enableAddButton, boolean enableRemoveButton) {
		var op = new Operation();
		op.setEvent(event);

		var operationItem = new OperationItem(operationList, op, event);
		operationItem.setAddButtonEnabled(enableAddButton);
		operationItem.setRemoveButtonEnabled(enableRemoveButton);
		operationList.addComponentAtIndex(position, operationItem);
		operationItem.updateTrackerLabel();
	}

	private Stream<OperationItem> getChildrenStream(VerticalLayout operationList) {
		return operationList.getChildren()
				.filter(OperationItem.class::isInstance)
				.map(OperationItem.class::cast);
	}

	private void enableAllAddButtons(VerticalLayout operationList) {
		getChildrenStream(operationList).forEach(item -> item.setAddButtonEnabled(true));
	}

	private void enableAllRemoveButtons(VerticalLayout operationList) {
		getChildrenStream(operationList).forEach(item -> item.setRemoveButtonEnabled(true));
	}

	private void updateAllTrackerLabels(VerticalLayout operationList) {
		getChildrenStream(operationList).forEach(OperationItem::updateTrackerLabel);
	}

	private void deleteEmptyOperationItems(VerticalLayout operationList, int omitPosition) {
		getChildrenStream(operationList).forEach(item -> {
			if (item.isEmpty() && operationList.indexOf(item) != omitPosition)
				operationList.remove(item);
		});
	}

	private void updateAddButtonStates(VerticalLayout operationList) {
		enableAllAddButtons(operationList);

		getChildrenStream(operationList).forEach(item -> {
			if (item.isEmpty()) {
				item.setAddButtonEnabled(false);

				var prevPos = operationList.indexOf(item) - 1;
				if (prevPos >= 0 && operationList.getComponentAt(prevPos) instanceof OperationItem prevItem)
					prevItem.setAddButtonEnabled(false);
			}
		});
	}

	class OperationItem extends HorizontalLayout {
		private VerticalLayout operationList;
		private ComboBox<Tracker> trackerBox = new ComboBox<>("Tracker");
		private HorizontalLayout menuBar = new HorizontalLayout();
		private Button addButton = new Button(new Icon(VaadinIcon.PLUS));
		private Button removeButton = new Button(new Icon(VaadinIcon.TRASH));

		private OperationItem(VerticalLayout operationList, Operation operation, Event event) {
			this.operationList = operationList;

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
				if (operation.getId() == null)
					enableAllAddButtons(operationList);

				// Get operation with updated version (operation has outdated version after saving in db trackerBox change event)
				Operation updatedOperation = service.findUpdatedOperation(operation);
				updatedOperation.setTracker(trackerEv.getValue());
				service.saveOperation(updatedOperation);

				removeButton.setEnabled(true);
			});

			addButton.addClickListener(e -> {
				if (isEmpty()) return;

				deleteEmptyOperationItems(operationList, operationList.indexOf(this));
				enableAllAddButtons(operationList);
				enableAllRemoveButtons(operationList);

				// Add new operation to GUI list but don't save it in db, it will be saved when user sets the tracker
				addNewOperationItem(operationList, event, operationList.indexOf(this) + 1, false, true);
				setAddButtonEnabled(false);
			});

			removeButton.addClickListener(e -> {
				// Get operation with updated version (operation has outdated version after saving in db trackerBox change event)
				service.deleteOperationById(operation.getId());
				operationList.remove(this);
				addNewOperationIfEmpty(operationList, event);

				updateAllTrackerLabels(operationList);
				updateAddButtonStates(operationList);

				// If last remaining item is empty -> disable its remove button
				var remainingItems = getChildrenStream(operationList).toList();
				if (remainingItems.size() == 1)
					remainingItems.getFirst().updateRemoveButtonState();
			});

			menuBar.add(addButton, removeButton);
			this.add(trackerBox, menuBar);
		}

		private boolean isEmpty() {
			return trackerBox.getValue() == null;
		}

		private void updateTrackerLabel() {
			trackerBox.setLabel(operationList.indexOf(this) == 0 ? "Tracker" : null);
		}

		// Call only if it's the last item in list
		private void updateRemoveButtonState() {
			setRemoveButtonEnabled(!isEmpty()); // disable if empty
		}

		private void setRemoveButtonEnabled(boolean state) {
			removeButton.setEnabled(state);
		}

		private void setAddButtonEnabled(boolean state) {
			addButton.setEnabled(state);
		}
	}
}
