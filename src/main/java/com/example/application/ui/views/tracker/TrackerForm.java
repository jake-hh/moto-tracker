package com.example.application.ui.views.tracker;

import com.example.application.data.BasicInterval;
import com.example.application.data.entity.Tracker;
import com.example.application.ui.components.Button;
import com.example.application.ui.events.LayoutFormEvent;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
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
				.withValidator(BasicInterval::isValid, BasicInterval.NOT_VALID_MSG)
				.bind(Tracker::getInterval, Tracker::setInterval);

		name.setRequired(true);
		name.setValueChangeMode(ValueChangeMode.LAZY);

		range.setValueChangeMode(ValueChangeMode.LAZY);
		range.setStepButtonsVisible(true);
		range.setStep(Tracker.RANGE_STEP);
		range.setMin(Tracker.RANGE_MIN);
		range.setMax(Tracker.RANGE_MAX);
		range.setSuffixComponent(new Span("km"));

		range.setI18n(
				new IntegerField.IntegerFieldI18n()
						.setStepErrorMessage(Tracker.RANGE_STEP_MSG)
						.setMinErrorMessage(Tracker.RANGE_MIN_MSG)
						.setMaxErrorMessage(Tracker.RANGE_MAX_MSG)
		);

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

		binder.addStatusChangeListener(e -> saveBtn.setActive(binder.isValid()));

		return new HorizontalLayout(saveBtn, deleteBtn, closeBtn);
	}

	public void setDeleteEnabled(boolean enabled) {
		deleteBtn.setEnabled(enabled);
		btnFooter.setText(enabled ? null : "Tracker is used in service history");
	}

	private void validateAndSave() {
		if (binder.isValid())
			fireEvent(new SaveEvent(this, binder.getBean()));
		else
			binder.validate();
	}

	public void setTracker(Tracker tracker) {
		deleteBtn.setVisible(!Tracker.isEmpty(tracker));
		binder.setBean(tracker);
	}


	  // --------------
	 // --- Events ---
	// --------------

	public static class SaveEvent extends LayoutFormEvent<Tracker> {
		SaveEvent(TrackerForm source, Tracker tracker) {
			super(source, tracker);
		}
	}

	public static class DeleteEvent extends LayoutFormEvent<Tracker> {
		DeleteEvent(TrackerForm source, Tracker tracker) {
			super(source, tracker);
		}
	}

	public static class CloseEvent extends LayoutFormEvent<Tracker> {
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
