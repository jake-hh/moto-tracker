package com.example.application.ui.views.vehicle;

import com.example.application.data.entity.Vehicle;
import com.example.application.services.MainService;
import com.example.application.ui.events.VehicleChangedEvent;
import com.example.application.ui.views.MainLayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import jakarta.annotation.security.PermitAll;


@SpringComponent
@UIScope
@PermitAll
@Route(value = "vehicles", layout = MainLayout.class)
@PageTitle("Vehicles | Moto Tracker")
public class VehicleView extends VerticalLayout {

	private final Grid<Vehicle> grid = new Grid<>(Vehicle.class, false);
	private final VehicleForm form = new VehicleForm();

	private final MainService service;


	public VehicleView(MainService service) {
		this.service = service;

		addClassName("view");
		setSizeFull();
		configureGrid();
		configureForm();

		add(getToolbar(), getContent());
		updateList();
		closeEditor();
	}

	private HorizontalLayout getContent() {
		HorizontalLayout content = new HorizontalLayout(grid, form);
		content.setFlexGrow(2, grid);
		content.setFlexGrow(1, form);
		content.addClassNames("content");
		content.setSizeFull();
		return content;
	}

	private void configureForm() {
		form.setWidth("25em");
		form.addSaveListener(this::saveVehicle);
		form.addDeleteListener(this::deleteVehicle);
		form.addCloseListener(e -> closeEditor());
	}

	private void saveVehicle(VehicleForm.SaveEvent event) {
		service.saveVehicle(event.getValue());
		updateList();
		closeEditor();
		fireEvent(new VehicleChangedEvent(this));
	}

	private void deleteVehicle(VehicleForm.DeleteEvent event) {
		service.deleteVehicle(event.getValue());
		updateList();
		closeEditor();
		fireEvent(new VehicleChangedEvent(this));
	}

	private void configureGrid() {
		grid.addClassNames("grid");
		grid.setSizeFull();
		grid.setColumns(
				"type",
				"make",
				"model",
				"engine",
				"colour",
				"plate",
				"vin",
				"productionDate",
				"registrationDate",
				"trackingDate"
		);

		grid.getColumnByKey("productionDate").setHeader("Production date");
		grid.getColumnByKey("registrationDate").setHeader("Registration date");
		grid.getColumnByKey("trackingDate").setHeader("Tracking since");

		grid.getColumns().forEach(col -> col.setAutoWidth(true));

		grid.asSingleSelect().addValueChangeListener(event ->
				editVehicle(event.getValue()));
	}

	private Component getToolbar() {
		Button addVehicleButton = new Button("Add vehicle");
		addVehicleButton.addClickListener(click -> addVehicle());

		var toolbar = new HorizontalLayout(addVehicleButton);
		toolbar.addClassName("toolbar");
		return toolbar;
	}

	public void editVehicle(Vehicle vehicle) {
		if (vehicle == null) {
			closeEditor();
			return;
		}

		form.setVehicle(vehicle);
		form.setVisible(true);
		addClassName("editing");

		boolean used = vehicle.getId() != null && service.isVehicleUsed(vehicle);
		form.setDeleteEnabled(!used);
	}

	private void closeEditor() {
		form.setVehicle(null);
		form.setVisible(false);
		removeClassName("editing");
	}

	private void addVehicle() {
		grid.asSingleSelect().clear();
		editVehicle(service.createVehicle());
	}

	private void updateList() {
		grid.setItems(service.findVehicles());
	}

	public void addVehicleChangedListener(ComponentEventListener<VehicleChangedEvent> listener) {
		addListener(VehicleChangedEvent.class, listener);
	}
}
