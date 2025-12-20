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
import java.util.Optional;


@SuppressWarnings("FieldMayBeFinal")
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
			// Get operation with updated version (operation has outdated version after saving in db trackerBox change event)
			Operation updatedOperation = service.findUpdatedOperation(operation);
			updatedOperation.setTracker(trackerEv.getValue());
			service.saveOperation(updatedOperation);

			eventItem.render();
		});

		addButton.addClickListener(e -> {
			if (isEmpty()) return;

			// Add new operation to GUI list but don't save it in db, it will be saved when user sets the tracker
			final int newPosition = operationList.indexOf(this) + 1;

			final int adjustPosition = eventItem.getEmptyPos()
					.filter(emptyPos -> emptyPos < newPosition)
					.isPresent() ? -1 : 0;

			eventItem.render(Optional.of(newPosition + adjustPosition));
		});

		removeButton.addClickListener(e -> {
			// Get operation with updated version (operation has outdated version after saving in db trackerBox change event)
			service.deleteOperationById(operation.getId());
			eventItem.render();
		});

		menuBar.add(addButton, removeButton);
		this.add(trackerBox, menuBar);
	}

	public boolean isEmpty() {
		return trackerBox.isEmpty();
	}

	public void updateTrackerLabel() {
		trackerBox.setLabel(operationList.indexOf(this) == 0 ? "Tracker" : null);
	}

	public void updateAddButton(OperationItem opItem, Optional<OperationItem> prevItem) {
		if (opItem.isEmpty()) {
			opItem.setAddButtonEnabled(false);
			prevItem.ifPresent(i -> i.setAddButtonEnabled(false));
		}
	}

	public void updateRemoveButton(long num_operations) {
		// Disable removeButton if it's the only item in list and is empty
		setRemoveButtonEnabled(num_operations != 1 || !isEmpty());
	}

	public void setRemoveButtonEnabled(boolean state) {
		removeButton.setEnabled(state);
	}

	public void setAddButtonEnabled(boolean state) {
		addButton.setEnabled(state);
	}
}
