package com.example.application.ui.views.vehicle;

import com.example.application.data.entity.Vehicle;
import com.example.application.services.MainService;
import com.example.application.services.UserSettingsService;
import com.example.application.ui.events.VehicleChangedEvent;
import com.example.application.ui.render.ColorCircleRenderer;
import com.example.application.ui.render.VehicleIconRenderer;
import com.example.application.ui.views.MainLayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import jakarta.annotation.security.PermitAll;


@SpringComponent
@UIScope
@PermitAll
@Route(value = "vehicles/:action?", layout = MainLayout.class)
@PageTitle("Vehicles | Moto Tracker")
public class VehicleView extends VerticalLayout implements BeforeEnterObserver {

	private final Grid<Vehicle> grid = new Grid<>(Vehicle.class, false);
	private final Span emptyLabel = new Span("No vehicles");
	private final VehicleForm form = new VehicleForm();

	private final MainService mainService;
	private final UserSettingsService settingsService;


	public VehicleView(MainService mainService, UserSettingsService settingsService) {
		this.mainService = mainService;
		this.settingsService = settingsService;

		ComponentUtil.addListener(UI.getCurrent(), VehicleChangedEvent.class, e -> updateList());

		addClassName("view");
		setSizeFull();
		configureGrid();
		configureForm();

		add(createToolbar(), createContent());
		updateList();
		closeEditor();
	}

	private Component createToolbar() {
		Button addVehicleButton = new Button("Add vehicle");
		addVehicleButton.addClickListener(click -> addVehicle());

		var toolbar = new HorizontalLayout(addVehicleButton);
		toolbar.addClassName("toolbar");
		return toolbar;
	}

	private Component createContent() {
		emptyLabel.addClassNames("mt-helper-text", "warning");

		var gridCol = new VerticalLayout(grid, emptyLabel);
		gridCol.setSizeFull();
		gridCol.setPadding(false);
		gridCol.setSpacing(false);

		var content = new HorizontalLayout(gridCol, form);
		content.setFlexGrow(2, gridCol);
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
		Vehicle vehicle = event.getValue();
		mainService.saveVehicle(vehicle);
		settingsService.getSelectedVehicle()
				.filter(sel -> sel.equals(vehicle))   // compares IDs
				.ifPresent(sel -> settingsService.updateSelectedVehicle(vehicle));
		closeEditor();
		ComponentUtil.fireEvent(UI.getCurrent(), new VehicleChangedEvent(UI.getCurrent(), vehicle));
	}

	private void deleteVehicle(VehicleForm.DeleteEvent event) {
		Vehicle vehicle = event.getValue();
		int trackerCount = mainService.countTrackersByVehicle(vehicle);

		if (trackerCount == 0) {
			doDeleteVehicle(vehicle);
		} else {
			openDeleteDialog(vehicle, trackerCount);
		}
	}

	private void openDeleteDialog(Vehicle vehicle, int trackerCount) {
		var dialog = new ConfirmDialog();
		if (trackerCount == 1) {
			dialog.setHeader("Vehicle has one tracker");
			dialog.setText("Do you want to delete it?");
		} else {
			dialog.setHeader("Vehicle has " + trackerCount + " trackers");
			dialog.setText("Do you want to delete them all?");
		}
		dialog.setCancelable(true);
		dialog.setConfirmText("Delete");
		dialog.setConfirmButtonTheme("error primary");

		dialog.addConfirmListener(ce -> doDeleteVehicleCascade(vehicle));
		dialog.open();
	}

	private void doDeleteVehicle(Vehicle vehicle) {
		mainService.deleteVehicle(vehicle);
		closeEditor();
		ComponentUtil.fireEvent(UI.getCurrent(), new VehicleChangedEvent(UI.getCurrent(), vehicle));
	}

	private void doDeleteVehicleCascade(Vehicle vehicle) {
		mainService.deleteVehicleCascade(vehicle);
		closeEditor();
		ComponentUtil.fireEvent(UI.getCurrent(), new VehicleChangedEvent(UI.getCurrent(), vehicle));
	}

	private void configureGrid() {
		grid.addClassNames("grid");
		grid.setSizeFull();

		grid.addColumn(new ComponentRenderer<>(VehicleIconRenderer::getIconByVehicle))
				.setHeader("Type")
				.setSortable(true)
				.setComparator(vehicle -> vehicle.getType().ordinal())
				.setFlexGrow(0);

		grid.addColumns(
				"make",
				"model",
				"engine"
		);

		grid.addColumn(new ComponentRenderer<>(
				vehicle -> ColorCircleRenderer.getCircle(vehicle.getColour())))
				.setHeader("Colour");

		grid.addColumns(
				"plate",
				"vin",
				"mileage"
		);

		grid.addColumn("productionDate").setHeader("Production date");
		grid.addColumn("registrationDate").setHeader("Registration date");
		grid.addColumn("trackingDate").setHeader("Tracking since");

		grid.getColumns().forEach(col -> col.setAutoWidth(true));

		grid.asSingleSelect().addValueChangeListener(event ->
				editVehicle(event.getValue()));
	}

	public void editVehicle(Vehicle vehicle) {
		if (vehicle == null) {
			closeEditor();
			return;
		}

		form.setVehicle(vehicle);
		form.setVisible(true);
		addClassName("editing");

		boolean used = vehicle.getId() != null && mainService.isVehicleUsed(vehicle);
		form.setDeleteEnabled(!used);
	}

	private void closeEditor() {
		form.setVehicle(null);
		form.setVisible(false);
		removeClassName("editing");
	}

	private void addVehicle() {
		grid.asSingleSelect().clear();
		editVehicle(mainService.createVehicle());
	}

	private void updateList() {
		var items = mainService.findVehicles();
		boolean empty = items.isEmpty();
		grid.setVisible(!empty);
		emptyLabel.setVisible(empty);
		grid.setItems(items);
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		event.getRouteParameters().get("action").ifPresent(action -> {
			if ("new".equals(action))
				addVehicle();
		});
	}
}
