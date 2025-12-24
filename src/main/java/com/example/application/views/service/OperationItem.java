package com.example.application.views.service;

import com.example.application.data.Operation;
import com.example.application.data.Tracker;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.List;
import java.util.function.Consumer;


@SuppressWarnings("FieldMayBeFinal")
public class OperationItem extends HorizontalLayout {

	private ComboBox<Tracker> trackerBox = new ComboBox<>();
	private Button addButton = new Button(new Icon(VaadinIcon.PLUS));
	private Button removeButton = new Button(new Icon(VaadinIcon.TRASH));

	public OperationItem(
			Runnable onAddButtonPressed,
			Runnable onRemoveButtonPressed,
			Consumer<Tracker> onTrackerBoxChanged,
			Operation operation,
			List<Tracker> trackers
	) {
		this.setAlignItems(Alignment.END);
		this.setSpacing(true);

		createTrackerBox(onTrackerBoxChanged, operation, trackers);
		createAddButton(onAddButtonPressed);
		createRemoveButton(onRemoveButtonPressed);

		var menuBar = new HorizontalLayout();
		menuBar.setSpacing(false);
		menuBar.setPadding(false);
		menuBar.add(addButton, removeButton);

		this.add(trackerBox, menuBar);
	}

	private void createTrackerBox(Consumer<Tracker> onTrackerBoxChanged, Operation operation, List<Tracker> trackers) {
		trackerBox.setItems(trackers);

		trackerBox.setItemLabelGenerator(Tracker::getName);
		trackerBox.setValue(operation.getTracker());

		trackerBox.addValueChangeListener(trackerEv -> onTrackerBoxChanged.accept(trackerEv.getValue()));
	}

	private void createAddButton(Runnable onAddButtonPressed) {
		addButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
		addButton.getElement().setAttribute("title", "Add new operation");

		addButton.addClickListener(e -> onAddButtonPressed.run());
	}

	private void createRemoveButton(Runnable onRemoveButtonPressed) {
		removeButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
		removeButton.getElement().setAttribute("title", "Remove this operation");

		removeButton.addClickListener(e -> onRemoveButtonPressed.run());
	}

	public void enableTrackerLabel(boolean enable) {
		if (enable)
			trackerBox.setLabel("Tracker");
	}

	public void disableRemoveButton(boolean disable) {
		if (disable)
			removeButton.setEnabled(false);
	}

	public void disableAddButton(boolean disable) {
		if (disable)
			addButton.setEnabled(false);
	}
}
