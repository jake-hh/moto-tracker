package com.example.application.views.service;

import com.example.application.data.Operation;
import com.example.application.data.Tracker;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.function.Consumer;


@SuppressWarnings("FieldMayBeFinal")
public class OperationItem extends HorizontalLayout {

	private Consumer<Integer> refreshEventItem;
	private EventItemController controller;
	private VerticalLayout operationList;
	@Nullable
	private Integer emptyPos;
	private List<Tracker> trackers;

	private ComboBox<Tracker> trackerBox = new ComboBox<>("Tracker");
	private Button addButton = new Button(new Icon(VaadinIcon.PLUS));
	private Button removeButton = new Button(new Icon(VaadinIcon.TRASH));

	public OperationItem(Consumer<Integer> refreshEventItem, EventItemController controller, VerticalLayout operationList, Operation operation, @Nullable Integer emptyPos, List<Tracker> trackers) {
		this.refreshEventItem = refreshEventItem;
		this.controller = controller;
		this.operationList = operationList;
		this.emptyPos = emptyPos;
		this.trackers = trackers;

		this.setAlignItems(Alignment.END);
		this.setSpacing(true);

		createTrackerBox(operation);
		createAddButton();
		createRemoveButton(operation);

		var menuBar = new HorizontalLayout();
		menuBar.setSpacing(false);
		menuBar.setPadding(false);
		menuBar.add(addButton, removeButton);

		this.add(trackerBox, menuBar);
	}

	private void createTrackerBox(Operation operation) {
		trackerBox.setItems(trackers);

		trackerBox.setItemLabelGenerator(Tracker::getName);
		trackerBox.setValue(operation.getTracker());

		trackerBox.addValueChangeListener(trackerEv -> {
			controller.updateOperation(operation, trackerEv.getValue());
			refreshEventItem.accept(null);
		});
	}

	private void createAddButton() {
		addButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
		addButton.getElement().setAttribute("title", "Add new operation");

		addButton.addClickListener(e -> {
			if (isEmpty()) return;

			int insertPos = operationList.indexOf(this) + 1;

			if (emptyPos != null && emptyPos < insertPos)
				insertPos--;

			// Add new operation to logic list but don't save it in db, it will be saved when user sets the tracker
			refreshEventItem.accept(insertPos);
		});
	}

	private void createRemoveButton(Operation operation) {
		removeButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
		removeButton.getElement().setAttribute("title", "Remove this operation");

		removeButton.addClickListener(e -> {
			controller.deleteOperation(operation);
			refreshEventItem.accept(null);
		});
	}

	public boolean isEmpty() {
		return trackerBox.isEmpty();
	}

	public void updateTrackerLabel() {
		trackerBox.setLabel(operationList.indexOf(this) == 0 ? "Tracker" : null);
	}

	public void updateAddButton(OperationItem opItem, @Nullable OperationItem prevItem) {
		if (opItem.isEmpty()) {
			opItem.setAddButtonEnabled(false);

			if (prevItem != null)
				prevItem.setAddButtonEnabled(false);
		}
	}

	public void updateRemoveButton(long num_operations) {
		// Disable removeButton if it's the only item in list and is empty
		removeButton.setEnabled(num_operations != 1 || !isEmpty());
	}

	public void setAddButtonEnabled(boolean state) {
		addButton.setEnabled(state);
	}
}
