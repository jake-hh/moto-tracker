package com.example.application.ui.views.vehicle;

import com.example.application.data.VehicleType;
import com.example.application.data.entity.Vehicle;
import com.example.application.ui.components.Button;
import com.example.application.ui.components.ColorPicker;
import com.example.application.ui.components.Footer;
import com.example.application.ui.events.LayoutFormEvent;

import com.example.application.ui.render.VehicleIconRenderer;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;


@SuppressWarnings("FieldCanBeLocal")
public class VehicleForm extends FormLayout {

	private final ComboBox<VehicleType> type = new ComboBox<>("Type");
	private final TextField make = new TextField("Make");
	private final TextField model = new TextField("Model");
	private final TextField engine = new TextField("Engine");
	private final ColorPicker colour = new ColorPicker("Colour");
	private final TextField plate = new TextField("Plate");
	private final TextField vin = new TextField("VIN number");
	private final IntegerField mileage = new IntegerField("Mileage");

	private final DatePicker productionDate = new DatePicker("Production date");
	private final DatePicker registrationDate = new DatePicker("Registration date");
	private final DatePicker trackingDate = new DatePicker("Tracking since");

	private final Button saveBtn = new Button("Save");
	private final Button deleteBtn = new Button("Delete");
	private final Button closeBtn = new Button("Cancel");

	private final Footer btnFooter = new Footer();

	private final Binder<Vehicle> binder = new BeanValidationBinder<>(Vehicle.class);

	public VehicleForm() {
		addClassName("form");

		make.setValueChangeMode(ValueChangeMode.LAZY);
		model.setValueChangeMode(ValueChangeMode.LAZY);
		engine.setValueChangeMode(ValueChangeMode.LAZY);
		plate.setValueChangeMode(ValueChangeMode.LAZY);
		vin.setValueChangeMode(ValueChangeMode.LAZY);
		mileage.setValueChangeMode(ValueChangeMode.LAZY);

		type.setRequired(true);
		make.setRequired(true);
		model.setRequired(true);
		engine.setRequired(true);
		mileage.setRequired(true);
		colour.setRequiredIndicatorVisible(true);

		// --- Set menu items ---
		type.setItems(VehicleType.values());

		// --- Render menu items ---
		type.setRenderer(new ComponentRenderer<>(VehicleIconRenderer::getDropdownItemsByVehicleType));

		// --- Update icon when value changes ---
		type.addValueChangeListener(click -> {
			var icon = VehicleIconRenderer.getSelectedVehicleIconByVehicleType(click.getValue());
			type.setPrefixComponent(icon);
		});

		// --- Initialize icon for existing value ---
		var icon = VehicleIconRenderer.getSelectedVehicleIconByVehicleType(type.getValue());
		type.setPrefixComponent(icon);

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
			createButtonBar(),
			btnFooter
		);
	}

	private Component createButtonBar() {
		saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
		closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

		saveBtn.addClickShortcut(Key.ENTER);
		closeBtn.addClickShortcut(Key.ESCAPE);

		saveBtn.setInactiveTooltipText("Invalid input");
		deleteBtn.setInactiveTooltipText("Vehicle is used in service history");
		btnFooter.setText("Cannot delete - Vehicle is used in service history");

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

	public void setVehicle(Vehicle vehicle) {
		binder.setBean(vehicle);
		deleteBtn.setVisible(!Vehicle.isEmpty(vehicle));
		btnFooter.showText(false);
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
