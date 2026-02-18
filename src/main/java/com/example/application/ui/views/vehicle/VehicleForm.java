package com.example.application.ui.views.vehicle;

import com.example.application.data.entity.Vehicle;
import com.example.application.ui.events.LayoutFormEvent;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;


@SuppressWarnings("FieldCanBeLocal")
public class VehicleForm extends FormLayout {

	private final TextField type = new TextField("Type");
	private final TextField make = new TextField("Make");
	private final TextField model = new TextField("Model");
	private final TextField engine = new TextField("Engine");
	private final TextField colour = new TextField("Colour");
	private final TextField plate = new TextField("Plate");
	private final TextField vin = new TextField("VIN number");
	private final IntegerField mileage = new IntegerField("Mileage");

	private final DatePicker productionDate = new DatePicker("Production date");
	private final DatePicker registrationDate = new DatePicker("Registration date");
	private final DatePicker trackingDate = new DatePicker("Tracking since");

	private final Button saveBtn = new Button("Save");
	private final Button deleteBtn = new Button("Delete");
	private final Button closeBtn = new Button("Cancel");
	private final Span removeStatusLabel = new Span();

	private final Binder<Vehicle> binder = new BeanValidationBinder<>(Vehicle.class);

	public VehicleForm() {
		addClassName("form");

		type.setValueChangeMode(ValueChangeMode.LAZY);
		make.setValueChangeMode(ValueChangeMode.LAZY);
		model.setValueChangeMode(ValueChangeMode.LAZY);
		engine.setValueChangeMode(ValueChangeMode.LAZY);
		colour.setValueChangeMode(ValueChangeMode.LAZY);
		plate.setValueChangeMode(ValueChangeMode.LAZY);
		vin.setValueChangeMode(ValueChangeMode.LAZY);
		mileage.setValueChangeMode(ValueChangeMode.LAZY);

		type.setRequired(true);
		make.setRequired(true);
		model.setRequired(true);
		engine.setRequired(true);
		colour.setRequired(true);
		mileage.setRequired(true);

		mileage.setStepButtonsVisible(true);
		mileage.setStep(Vehicle.MILEAGE_STEP);
		mileage.setMin(Vehicle.MILEAGE_MIN);
		mileage.setMax(Vehicle.MILEAGE_MAX);
		mileage.setSuffixComponent(new Span("km"));

		mileage.setI18n(
				new IntegerField.IntegerFieldI18n()
						.setStepErrorMessage(Vehicle.MILEAGE_STEP_MSG)
						.setMinErrorMessage(Vehicle.MILEAGE_MIN_MSG)
						.setMaxErrorMessage(Vehicle.MILEAGE_MAX_MSG)
		);

		//addStatusLabel.addClassNames("mt-helper-text", "warning");
		removeStatusLabel.addClassName("mt-helper-text");

		binder.bindInstanceFields(this);

		add(
			type,
			make,
			model,
			engine,
			colour,
			plate,
			vin,
			mileage,
			productionDate,
			registrationDate,
			trackingDate,
			createButtonsLayout(),
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

		binder.addStatusChangeListener(e ->
				saveBtn.getClassNames().set("mt-inactive-btn", !binder.isValid()));

		return new HorizontalLayout(saveBtn, deleteBtn, closeBtn);
	}

	public void setDeleteEnabled(boolean enabled) {
		deleteBtn.setEnabled(enabled);
		removeStatusLabel.setText(enabled ? null : "Vehicle is used in service history");
	}

	private void validateAndSave() {
		if (binder.isValid())
			fireEvent(new SaveEvent(this, binder.getBean()));
		else
			binder.validate();
	}

	public void setVehicle(Vehicle vehicle) {
		deleteBtn.setVisible(!Vehicle.isEmpty(vehicle));
		binder.setBean(vehicle);
	}


	  // --------------
	 // --- Events ---
	// --------------

	public static class SaveEvent extends LayoutFormEvent<Vehicle> {
		SaveEvent(VehicleForm source, Vehicle vehicle) {
			super(source, vehicle);
		}
	}

	public static class DeleteEvent extends LayoutFormEvent<Vehicle> {
		DeleteEvent(VehicleForm source, Vehicle vehicle) {
			super(source, vehicle);
		}
	}

	public static class CloseEvent extends LayoutFormEvent<Vehicle> {
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
