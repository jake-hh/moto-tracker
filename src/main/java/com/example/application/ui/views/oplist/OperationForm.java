package com.example.application.ui.views.oplist;

import com.example.application.data.entity.*;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
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

	private final Button save = new Button("Save");
	private final Button delete = new Button("Delete");
	private final Button close = new Button("Cancel");

	private final Binder<Operation> binder = new BeanValidationBinder<>(Operation.class);

	public OperationForm() {
		addClassName("operation-form");
		binder.bindInstanceFields(this);

		tracker.setItemLabelGenerator(Tracker::getName);
		event.setItemLabelGenerator(Event::getDateStr);

		add(tracker, event, createButtonsLayout());
	}

	public void setTrackers(List<Tracker> trackers) {
		tracker.setItems(trackers);
	}

	public void setEvents(List<Event> events) {
		event.setItems(events);
	}

	private Component createButtonsLayout() {
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
		close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

		save.addClickShortcut(Key.ENTER);
		close.addClickShortcut(Key.ESCAPE);

		save.addClickListener(event -> validateAndSave());
		delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
		close.addClickListener(event -> fireEvent(new CloseEvent(this)));

		binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

		return new HorizontalLayout(save, delete, close);
	}

	private void validateAndSave() {
		if (binder.isValid())
			fireEvent(new SaveEvent(this, binder.getBean()));
	}


	public void setOperation(Operation operation) {
		binder.setBean(operation);
	}


	  // --------------
	 // --- Events ---
	// --------------

	public static abstract class OperationFormEvent extends ComponentEvent<OperationForm> {
		private final Operation operation;

		protected OperationFormEvent(OperationForm source, Operation operation) {
			super(source, false);
			this.operation = operation;
		}

		public Operation getOperation() {
			return operation;
		}
	}

	public static class SaveEvent extends OperationFormEvent {
		SaveEvent(OperationForm source, Operation operation) {
			super(source, operation);
		}
	}

	public static class DeleteEvent extends OperationFormEvent {
		DeleteEvent(OperationForm source, Operation operation) {
			super(source, operation);
		}

	}

	public static class CloseEvent extends OperationFormEvent {
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
