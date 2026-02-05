package com.example.application.ui.views.dashboard;

import com.example.application.data.DashboardEventFormat;
import com.example.application.data.entity.Tracker;
import com.example.application.data.entity.Vehicle;
import com.example.application.services.model.TrackerData;
import com.example.application.services.MainService;
import com.example.application.services.UserSettingsService;
import com.example.application.ui.events.EventChangedEvent;
import com.example.application.ui.events.OperationChangedEvent;
import com.example.application.ui.events.TrackerChangedEvent;
import com.example.application.ui.events.VehicleSelectedEvent;
import com.example.application.ui.render.TrackerDataRenderer;
import com.example.application.ui.views.MainLayout;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import jakarta.annotation.security.PermitAll;

import java.util.List;


@SpringComponent
@UIScope
@PermitAll
@Route(value = "", layout = MainLayout.class)
@PageTitle("Dashboard | Moto Tracker")
public class DashboardView extends VerticalLayout {

	private final Grid<Tracker> grid = new Grid<>(Tracker.class, false);
	private final VerticalLayout header = new VerticalLayout();
	private final RadioButtonGroup<DashboardEventFormat> formatSelect = new RadioButtonGroup<>();

	private final MainService mainService;
	private final UserSettingsService settingsService;


	public DashboardView(
			MainService mainService,
			UserSettingsService settingsService,
			MainLayout mainLayout
	) {
		this.mainService = mainService;
		this.settingsService = settingsService;

		mainLayout.addVehicleSelectedListener(this::onVehicleSelected);
		mainLayout.addTrackerChangedListener(this::onTrackerChanged);
		mainLayout.addEventChangedListener(this::onEventChanged);
		mainLayout.addOperationChangedListener(this::onOperationChanged);

		addClassName("view");
		setSizeFull();
		configureGrid();

		add(header, getToolbar(), grid);
		//grid.setSizeFull();

		update();
	}

	private void onVehicleSelected(VehicleSelectedEvent e) {
		update();
	}

	private void onTrackerChanged(TrackerChangedEvent e) {
		update();
	}

	private void onEventChanged(EventChangedEvent e) {
		update();
	}

	private void onOperationChanged(OperationChangedEvent e) {
		update();
	}

	private void configureGrid() {
		grid.addClassNames("grid");
		grid.setSizeFull();
		grid.setColumns("name", "interval", "range");

		grid.addColumn(tracker -> "").setKey("date");
		grid.addColumn(tracker -> "").setKey("mileage");

		grid.getColumns().forEach(col -> col.setAutoWidth(true));
	}

	private HorizontalLayout getToolbar() {
		formatSelect.setLabel("Service format");
		formatSelect.setItems(DashboardEventFormat.values());
		formatSelect.setItemLabelGenerator(DashboardEventFormat::getLabel);
		formatSelect.addValueChangeListener(this::onFormatSelectChange);

		var toolbar = new HorizontalLayout(formatSelect);
		toolbar.addClassName("toolbar");
		return toolbar;
	}

	private void onFormatSelectChange(ValueChangeEvent<DashboardEventFormat> change) {
		if (!change.isFromClient()) return;

		settingsService.updateDashboardEventFormat(change.getValue());
		updateHeader();
		updateList();
	}

	private void update() {
		formatSelect.setValue(settingsService.getDashboardEventFormat());
		updateHeader();
		updateList();
	}

	private void updateHeader() {
		String vehicleName = settingsService.getSelectedVehicle().map(Vehicle::toStringShort).orElse("No vehicle has been selected");
		String vehicleData = settingsService.getSelectedVehicle().map(Vehicle::toString).orElse(" ");

		header.removeAll();
		header.add(
				new H1(vehicleName),
				new NativeLabel(vehicleData)
		);
	}

	private String getDateColumnHeader(DashboardEventFormat format) {
		return switch (format) {
			case LAST_SERVICE -> "Last date";
			case NEXT_SERVICE -> "Next date";
			case NEXT_SERVICE_RELATIVE -> "Remaining time";
		};
	}

	private String getMileageColumnHeader(DashboardEventFormat format) {
		return switch (format) {
			case LAST_SERVICE -> "Last mileage";
			case NEXT_SERVICE -> "Next mileage";
			case NEXT_SERVICE_RELATIVE -> "Remaining distance";
		};
	}

	private void updateList() {
		DashboardEventFormat format = settingsService.getDashboardEventFormat();
		List<Tracker> trackers = mainService.findTrackers();
		TrackerData data = mainService.loadDataForTrackers(trackers);

		grid.setItems(trackers);

		grid.getColumnByKey("date")
				.setHeader(getDateColumnHeader(format))
				.setRenderer(TrackerDataRenderer.renderDate(data, format));

		grid.getColumnByKey("mileage")
				.setHeader(getMileageColumnHeader(format))
				.setRenderer(TrackerDataRenderer.renderMileage(data, format));

	}
}
