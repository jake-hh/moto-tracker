package com.example.application.ui.views.vehicle;

import com.example.application.data.BasicPolicy;
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

		mileage.setStepButtonsVisible(true);
		mileage.setStep(100);
		mileage.setSuffixComponent(new Span("km"));
		//mileageField.setHelperText("km");

		mileage.setI18n(new IntegerField.IntegerFieldI18n()
				.setBadInputErrorMessage("Invalid number format")
				.setStepErrorMessage("Number must be a multiple of 100"));

		//addStatusLabel.addClassNames("mt-helper-text", "warning");
		removeStatusLabel.addClassName("mt-helper-text");

		bindFields();

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

	void bindFields() {
		//binder.bindInstanceFields(this);

		binder.forField(type)
				.asRequired("Vehicle type is required")
				.withValidator(BasicPolicy::isAlphabetic, "Vehicle type must be alphabetic")
				.withValidator(BasicPolicy::isStrEmptyOrLongEnough, "Vehicle type is too short")
				.withValidator(BasicPolicy::isStrEmptyOrNotTooLong, "Vehicle type is too long")
				.bind(Vehicle::getType, Vehicle::setType);

		binder.forField(make)
				.asRequired("Vehicle make is required")
				.withValidator(BasicPolicy::isAlphabetic, "Vehicle make must be alphabetic")
				.withValidator(BasicPolicy::isStrEmptyOrLongEnough, "Vehicle make is too short")
				.withValidator(BasicPolicy::isStrEmptyOrNotTooLong, "Vehicle make is too long")
				.bind(Vehicle::getMake, Vehicle::setMake);

		binder.forField(model)
				.asRequired("Vehicle model is required")
				.withValidator(BasicPolicy::isAlphanumeric, "Vehicle model must be alphanumeric")
				.withValidator(BasicPolicy::isStrEmptyOrLongEnough, "Vehicle model is too short")
				.withValidator(BasicPolicy::isStrEmptyOrNotTooLong, "Vehicle model is too long")
				.bind(Vehicle::getModel, Vehicle::setModel);

		binder.forField(engine)
				.asRequired("Vehicle engine is required")
				.withValidator(BasicPolicy::isStrEmptyOrLongEnough, "Vehicle engine is too short")
				.withValidator(BasicPolicy::isStrEmptyOrNotTooLong, "Vehicle engine is too long")
				.bind(Vehicle::getEngine, Vehicle::setEngine);

		binder.forField(colour)
				.asRequired("Vehicle colour is required")
				.withValidator(BasicPolicy::isAlphabetic, "Vehicle colour must be alphabetic")
				.withValidator(BasicPolicy::isStrEmptyOrLongEnough, "Vehicle colour is too short")
				.withValidator(BasicPolicy::isStrEmptyOrNotTooLong, "Vehicle colour is too long")
				.bind(Vehicle::getColour, Vehicle::setColour);

		binder.forField(plate)
				.asRequired("Vehicle plate is required")
				.withValidator(BasicPolicy::isAlphanumeric, "Vehicle plate must be alphanumeric")
				.withValidator(BasicPolicy::isStrEmptyOrLongEnough, "Vehicle plate is too short")
				.withValidator(BasicPolicy::isStrEmptyOrNotTooLong, "Vehicle plate is too long")
				.bind(Vehicle::getPlate, Vehicle::setPlate);

		binder.forField(vin)
				.asRequired("Vehicle vin is required")
				.withValidator(BasicPolicy::isAlphanumeric, "Vehicle vin must be alphanumeric")
				.withValidator(BasicPolicy::isStrEmptyOrLongEnough, "Vehicle vin is too short")
				.withValidator(BasicPolicy::isStrEmptyOrNotTooLong, "Vehicle vin is too long")
				.bind(Vehicle::getVin, Vehicle::setVin);

		binder.forField(mileage)
				.asRequired("Vehicle mileage is required")
				.bind(Vehicle::getMileage, Vehicle::setMileage);
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

		return new HorizontalLayout(saveBtn, deleteBtn, closeBtn);
	}

	public void setDeleteEnabled(boolean enabled) {
		deleteBtn.setEnabled(enabled);
		removeStatusLabel.setText(enabled ? null : "Vehicle is used in service history");
	}

	private void validateAndSave() {
		Vehicle vehicle = new Vehicle();

		if (binder.writeBeanIfValid(vehicle)) // validates and reads fields
			fireEvent(new SaveEvent(this, binder.getBean()));
	}

	public void setVehicle(Vehicle vehicle) {
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
