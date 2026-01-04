package com.example.application.views.vehicle;

import com.example.application.data.Vehicle;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;


@SuppressWarnings("FieldCanBeLocal")
public class VehicleForm extends FormLayout {

	private final TextField type = new TextField("type");
	private final TextField make = new TextField("make");
	private final TextField model = new TextField("model");
	private final TextField engine = new TextField("engine");
	private final TextField colour = new TextField("colour");
	private final TextField plate = new TextField("plate");
	private final TextField vin = new TextField("vin");

	private final DatePicker productionDate = new DatePicker("Production date");
	private final DatePicker registrationDate = new DatePicker("Registration date");
	private final DatePicker trackingDate = new DatePicker("Tracking date");

	private final Button saveBtn = new Button("Save");
	private final Button deleteBtn = new Button("Delete");
	private final Button closeBtn = new Button("Cancel");
	private final Span addStatusLabel = new Span();
	private final Span removeStatusLabel = new Span();

	private final Binder<Vehicle> binder = new BeanValidationBinder<>(Vehicle.class);

	public VehicleForm() {
		addClassName("vehicle-form");
		binder.bindInstanceFields(this);

		type.setValueChangeMode(ValueChangeMode.EAGER);
		make.setValueChangeMode(ValueChangeMode.EAGER);
		model.setValueChangeMode(ValueChangeMode.EAGER);
		engine.setValueChangeMode(ValueChangeMode.EAGER);
		colour.setValueChangeMode(ValueChangeMode.EAGER);
		plate.setValueChangeMode(ValueChangeMode.EAGER);
		vin.setValueChangeMode(ValueChangeMode.EAGER);

		trackingDate.addValueChangeListener(e -> binder.validate());

		addStatusLabel.addClassNames("mt-helper-text", "warning");
		removeStatusLabel.addClassName("mt-helper-text");

		binder.addStatusChangeListener(e -> {
			saveBtn.setEnabled(binder.isValid());
			addStatusLabel.setText(binder.isValid() ? "" : "Please fill in all required fields");
		});

		add(
			type,
			make,
			model,
			engine,
			colour,
			plate,
			vin,
			productionDate,
			registrationDate,
			trackingDate,
			createButtonsLayout(),
			addStatusLabel,
			removeStatusLabel
		);
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
		removeStatusLabel.setText(enabled ? null : "Vehicle is used in service history");
	}

	private void validateAndSave() {
		if (binder.isValid())
			fireEvent(new SaveEvent(this, binder.getBean()));
	}

	public void setVehicle(Vehicle vehicle) {
		binder.setBean(vehicle);
	}


	  // --------------
	 // --- Events ---
	// --------------

	public static abstract class VehicleFormEvent extends ComponentEvent<VehicleForm> {
		private final Vehicle vehicle;

		protected VehicleFormEvent(VehicleForm source, Vehicle vehicle) {
			super(source, false);
			this.vehicle = vehicle;
		}

		public Vehicle getVehicle() {
			return vehicle;
		}
	}

	public static class SaveEvent extends VehicleFormEvent {
		SaveEvent(VehicleForm source, Vehicle vehicle) {
			super(source, vehicle);
		}
	}

	public static class DeleteEvent extends VehicleFormEvent {
		DeleteEvent(VehicleForm source, Vehicle vehicle) {
			super(source, vehicle);
		}

	}

	public static class CloseEvent extends VehicleFormEvent {
		CloseEvent(VehicleForm source) {
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
