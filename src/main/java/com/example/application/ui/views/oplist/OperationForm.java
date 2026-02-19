package com.example.application.ui.views.oplist;

import com.example.application.data.entity.*;
import com.example.application.ui.components.Button;
import com.example.application.ui.components.Footer;
import com.example.application.ui.events.LayoutFormEvent;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;

import java.util.List;


@SuppressWarnings("FieldCanBeLocal")
public class OperationForm extends FormLayout {
	private final ComboBox<Event> event = new ComboBox<>("Event");
	private final ComboBox<Tracker> tracker = new ComboBox<>("Tracker");

	private final Button saveBtn = new Button("Save");
	private final Button deleteBtn = new Button("Delete");
	private final Button closeBtn = new Button("Cancel");

	private final Footer btnFooter = new Footer();

	private final Binder<Operation> binder = new BeanValidationBinder<>(Operation.class);

	public OperationForm() {
		addClassName("form");

		binder.bindInstanceFields(this);

		tracker.setItemLabelGenerator(Tracker::getName);
		event.setItemLabelGenerator(Event::getDateStr);

		add(tracker, event, createButtonsLayout(), btnFooter);
	}

	public void setTrackers(List<Tracker> trackers) {
		tracker.setItems(trackers);
	}

	public void setEvents(List<Event> events) {
		event.setItems(events);
	}

	private Component createButtonsLayout() {
		saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
		closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

		saveBtn.addClickShortcut(Key.ENTER);
		closeBtn.addClickShortcut(Key.ESCAPE);

		saveBtn.setInactiveTooltipText("Invalid input");

		saveBtn.addClickListener(event -> validateAndSave());
		deleteBtn.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
		closeBtn.addClickListener(event -> fireEvent(new CloseEvent(this)));

		binder.addStatusChangeListener(e -> saveBtn.setActive(binder.isValid()));

		var layout = new HorizontalLayout(saveBtn, deleteBtn, closeBtn);
		layout.setClassName("mt-form-btn-layout");
		return layout;
	}

	private void validateAndSave() {
		if (binder.isValid())
			fireEvent(new SaveEvent(this, binder.getBean()));
		else
			binder.validate();
	}


	public void setOperation(Operation operation) {
		deleteBtn.setVisible(!Operation.isEmpty(operation));
		binder.setBean(operation);
	}


	  // --------------
	 // --- Events ---
	// --------------

	public static class SaveEvent extends LayoutFormEvent<Operation> {
		SaveEvent(OperationForm source, Operation operation) {
			super(source, operation);
		}
	}

	public static class DeleteEvent extends LayoutFormEvent<Operation> {
		DeleteEvent(OperationForm source, Operation operation) {
			super(source, operation);
		}
	}

	public static class CloseEvent extends LayoutFormEvent<Operation> {
		CloseEvent(OperationForm source) {
			super(source, null);
		}
	}

	public void addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
		addListener(DeleteEvent.class, listener);
	}

	public void addSaveListener(ComponentEventListener<SaveEvent> listener) {
		addListener(SaveEvent.class, listener);
	}

	public void addCloseListener(ComponentEventListener<CloseEvent> listener) {
		addListener(CloseEvent.class, listener);
	}
}
