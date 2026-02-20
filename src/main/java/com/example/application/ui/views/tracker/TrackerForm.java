package com.example.application.ui.views.tracker;

import com.example.application.data.BasicInterval;
import com.example.application.data.entity.Tracker;
import com.example.application.ui.components.Button;
import com.example.application.ui.components.Footer;
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
	private final Footer btnFooter = new Footer();

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

		add(name, range, intervalField, createButtonBar(), btnFooter);
	}

	private Component createButtonBar() {
		saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
		closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

		saveBtn.addClickShortcut(Key.ENTER);
		closeBtn.addClickShortcut(Key.ESCAPE);

		saveBtn.setInactiveTooltipText("Invalid input");
		deleteBtn.setInactiveTooltipText("Tracker is used in service history");
		btnFooter.setText("Cannot delete - Tracker is used in service history");

		saveBtn.addClickListener(event -> validateAndSave());
		deleteBtn.addClickListener(event -> checkAndDelete());
		closeBtn.addClickListener(event -> fireEvent(new CloseEvent(this)));

		binder.addStatusChangeListener(e -> saveBtn.setActive(binder.isValid()));

		var bar = new HorizontalLayout(saveBtn, deleteBtn, closeBtn);
		bar.addClassName("mt-form-btn-bar");
		return bar;
	}

	public void setDeleteEnabled(boolean enabled) {
		deleteBtn.setActive(enabled);
	}

	private void checkAndDelete() {
		if (deleteBtn.isActive())
			fireEvent(new DeleteEvent(this, binder.getBean()));
		else
			btnFooter.showText(true);
	}

	private void validateAndSave() {
		if (binder.isValid())
			fireEvent(new SaveEvent(this, binder.getBean()));
		else
			binder.validate();
	}

	public void setTracker(Tracker tracker) {
		binder.setBean(tracker);
		deleteBtn.setVisible(!Tracker.isEmpty(tracker));
		btnFooter.showText(false);
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
