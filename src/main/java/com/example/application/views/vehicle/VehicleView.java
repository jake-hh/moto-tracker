package com.example.application.views.vehicle;

import com.example.application.data.Vehicle;
import com.example.application.services.MainService;
import com.example.application.views.MainLayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;

import jakarta.annotation.security.PermitAll;
import org.springframework.context.annotation.Scope;


@SpringComponent
@Scope("prototype")
@PermitAll
@Route(value = "vehicles", layout = MainLayout.class)
@PageTitle("Vehicles | Moto Vehicle")
public class VehicleView extends VerticalLayout {

	private final Grid<Vehicle> grid = new Grid<>(Vehicle.class, false);
	private final VehicleForm form = new VehicleForm();
	private final MainService service;


	public VehicleView(MainService service) {
		this.service = service;
		addClassName("vehicle-view");
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
		content.addClassNames("vehicle-content");
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
		service.saveVehicle(event.getVehicle());
		updateList();
		closeEditor();
	}

	private void deleteVehicle(VehicleForm.DeleteEvent event) {
		service.deleteVehicle(event.getVehicle());
		updateList();
		closeEditor();
	}

	private void configureGrid() {
		grid.addClassNames("vehicle-grid");
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
		toolbar.addClassName("vehicle-toolbar");
		return toolbar;
	}

	public void editVehicle(Vehicle vehicle) {
		if (vehicle == null) {
			closeEditor();
			return;
		}

		form.setVehicle(vehicle);
		form.setVisible(true);
		addClassName("vehicle-editing");

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
		var vehicle = new Vehicle();
		vehicle.setTrackingDate(MainService.getDateToday());
		editVehicle(vehicle);
	}

	private void updateList() {
		grid.setItems(service.findVehicles());
	}
}
