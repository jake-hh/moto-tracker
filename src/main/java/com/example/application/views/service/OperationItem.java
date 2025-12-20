package com.example.application.views.service;

import com.example.application.data.Event;
import com.example.application.data.Operation;
import com.example.application.data.Tracker;
import com.example.application.services.MainService;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;


public class OperationItem extends HorizontalLayout {
	private Event event;
	private EventItem eventItem;
	private List<Tracker> trackers;
	private MainService service;
	private VerticalLayout operationList;
	private ComboBox<Tracker> trackerBox = new ComboBox<>("Tracker");
	private HorizontalLayout menuBar = new HorizontalLayout();
	private Button addButton = new Button(new Icon(VaadinIcon.PLUS));
	private Button removeButton = new Button(new Icon(VaadinIcon.TRASH));

	public OperationItem(EventItem eventItem, List<Tracker> trackers, MainService service, VerticalLayout operationList, Operation operation, Event event) {
		this.event = event;
		this.eventItem = eventItem;
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
				eventItem.enableAllAddButtons(operationList);

			// Get operation with updated version (operation has outdated version after saving in db trackerBox change event)
			Operation updatedOperation = service.findUpdatedOperation(operation);
			updatedOperation.setTracker(trackerEv.getValue());
			service.saveOperation(updatedOperation);

			removeButton.setEnabled(true);
		});

		addButton.addClickListener(e -> {
			if (isEmpty()) return;

			eventItem.deleteEmptyOperationItems(operationList, operationList.indexOf(this));
			eventItem.enableAllAddButtons(operationList);
			eventItem.enableAllRemoveButtons(operationList);

			// Add new operation to GUI list but don't save it in db, it will be saved when user sets the tracker
			eventItem.addNewOperationItem(operationList, event, operationList.indexOf(this) + 1, false, true);
			setAddButtonEnabled(false);
		});

		removeButton.addClickListener(e -> {
			// Get operation with updated version (operation has outdated version after saving in db trackerBox change event)
			service.deleteOperationById(operation.getId());
			operationList.remove(this);
			eventItem.addNewOperationIfEmpty(operationList, event);

			eventItem.updateAllTrackerLabels(operationList);
			eventItem.updateAddButtonStates(operationList);

			// If last remaining item is empty -> disable its remove button
			var remainingItems = eventItem.getChildrenStream(operationList).toList();
			if (remainingItems.size() == 1)
				remainingItems.getFirst().updateRemoveButtonState();
		});

		menuBar.add(addButton, removeButton);
		this.add(trackerBox, menuBar);
	}

	public boolean isEmpty() {
		return trackerBox.getValue() == null;
	}

	public void updateTrackerLabel() {
		trackerBox.setLabel(operationList.indexOf(this) == 0 ? "Tracker" : null);
	}

	// Call only if it's the last item in list
	private void updateRemoveButtonState() {
		setRemoveButtonEnabled(!isEmpty()); // disable if empty
	}

	public void setRemoveButtonEnabled(boolean state) {
		removeButton.setEnabled(state);
	}

	public void setAddButtonEnabled(boolean state) {
		addButton.setEnabled(state);
	}
}
