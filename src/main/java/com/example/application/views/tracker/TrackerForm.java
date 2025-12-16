package com.example.application.views.tracker;

import com.example.application.data.Tracker;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;


public class TrackerForm extends FormLayout {
  TextField name = new TextField("Name");
  IntegerField range = new IntegerField("Range"); // range.setLabel("X");
  IntervalField interv = new IntervalField("Interval");

  Button save = new Button("Save");
  Button delete = new Button("Delete");
  Button close = new Button("Cancel");

  Binder<Tracker> binder = new BeanValidationBinder<>(Tracker.class);

  public TrackerForm() {
	addClassName("tracker-form");
	binder.bindInstanceFields(this);

	binder.forField(interv)
			.withValidator(i -> i == null || i.isValid(), "Amount and unit must both be set or both empty")
			.bind(Tracker::getInterval, Tracker::setInterval);

	add(name,
		range,
		interv,
		createButtonsLayout());
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
	if(binder.isValid()) {
	  fireEvent(new SaveEvent(this, binder.getBean()));
	}
  }


  public void setTracker(Tracker tracker) {
	binder.setBean(tracker);
  }

  // Events
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
