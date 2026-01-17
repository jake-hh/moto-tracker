package com.example.application.ui.views.tracker;

import com.example.application.data.entity.Tracker;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;


@SuppressWarnings("FieldCanBeLocal")
public class TrackerForm extends FormLayout {

	private final TextField name = new TextField("Name");
	private final IntegerField range = new IntegerField("Range"); // range.setLabel("X");
	private final IntervalField intervalField = new IntervalField("Interval");

	private final Button saveBtn = new Button("Save");
	private final Button deleteBtn = new Button("Delete");
	private final Button closeBtn = new Button("Cancel");
	private final Span btnFooter = new Span();

	private final Binder<Tracker> binder = new BeanValidationBinder<>(Tracker.class);

	public TrackerForm() {
		addClassName("form");
		binder.bindInstanceFields(this);

		binder.forField(intervalField)
				.withValidator(i -> i == null || i.isValid(), "amount and unit must both be set or both empty")
				.bind(Tracker::getInterval, Tracker::setInterval);

		name.setValueChangeMode(ValueChangeMode.LAZY);

		range.setValueChangeMode(ValueChangeMode.LAZY);
		range.setStepButtonsVisible(true);
		range.setStep(100);
		range.setMin(100);

		btnFooter.addClassName("mt-helper-text");

		add(name, range, intervalField, createButtonsLayout(), btnFooter);
	}

	private Component createButtonsLayout() {
		saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
		closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

		saveBtn.addClickShortcut(Key.ENTER);
		closeBtn.addClickShortcut(Key.ESCAPE);

		saveBtn.addClickListener(event -> validateAndSave());
		deleteBtn.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
		closeBtn.addClickListener(event -> fireEvent(new CloseEvent(this)));

		binder.addStatusChangeListener(e -> saveBtn.setEnabled(binder.isValid()));

		return new HorizontalLayout(saveBtn, deleteBtn, closeBtn);
	}

	public void setDeleteEnabled(boolean enabled) {
		deleteBtn.setEnabled(enabled);
		btnFooter.setText(enabled ? null : "Tracker is used in service history");
	}

	private void validateAndSave() {
		if (binder.isValid())
			fireEvent(new SaveEvent(this, binder.getBean()));
	}

	public void setTracker(Tracker tracker) {
		binder.setBean(tracker);
	}


	  // --------------
	 // --- Events ---
	// --------------

	public static abstract class TrackerFormEvent extends ComponentEvent<TrackerForm> {
		private final Tracker tracker;

		protected TrackerFormEvent(TrackerForm source, Tracker tracker) {
			super(source, false);
			this.tracker = tracker;
		}

		public Tracker getTracker() {
			return tracker;
		}
	}

	public static class SaveEvent extends TrackerFormEvent {
		SaveEvent(TrackerForm source, Tracker tracker) {
			super(source, tracker);
		}
	}

	public static class DeleteEvent extends TrackerFormEvent {
		DeleteEvent(TrackerForm source, Tracker tracker) {
			super(source, tracker);
		}

	}

	public static class CloseEvent extends TrackerFormEvent {
		CloseEvent(TrackerForm source) {
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
