package com.example.application.ui.views.service;

import com.example.application.data.entity.Tracker;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.function.Consumer;


public class OperationItem extends HorizontalLayout {

	private final ComboBox<Tracker> trackerBox = new ComboBox<>();
	private final Button addButton = new Button(new Icon(VaadinIcon.PLUS));
	private final Button removeButton = new Button(new Icon(VaadinIcon.TRASH));


	public OperationItem(@Nullable Tracker selectedTracker, List<Tracker> trackers) {
		this.setAlignItems(Alignment.END);
		this.setSpacing(true);

		trackerBox.setItems(trackers);
		trackerBox.setItemLabelGenerator(Tracker::getName);
		trackerBox.setValue(selectedTracker);

		this.add(trackerBox, createEditBar());
	}

	private Component createEditBar() {
		var bar = new HorizontalLayout();
		bar.setSpacing(false);
		bar.setPadding(false);
		bar.add(addButton, removeButton);

		addButton.addThemeVariants(
				ButtonVariant.LUMO_ICON,
				ButtonVariant.LUMO_TERTIARY
		);
		addButton.getElement().setAttribute("title", "Add new operation");

		removeButton.addThemeVariants(
				ButtonVariant.LUMO_ICON,
				ButtonVariant.LUMO_ERROR,
				ButtonVariant.LUMO_TERTIARY
		);
		removeButton.getElement().setAttribute("title", "Remove this operation");

		return bar;
	}

	public void onTrackerBoxChanged(Consumer<Tracker> callback) {
		trackerBox.addValueChangeListener(trackerVCE -> {
			if (trackerVCE.isFromClient())
				callback.accept(trackerVCE.getValue());
		});
	}

	public void onAddButtonPressed(Runnable callback) {
		addButton.addClickListener(e -> callback.run());
	}

	public void onRemoveButtonPressed(Runnable callback) {
		removeButton.addClickListener(e -> callback.run());
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
