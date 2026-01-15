package com.example.application.views.dashboard;

import com.example.application.data.DashboardEventFormat;
import com.example.application.data.entity.Tracker;
import com.example.application.data.entity.Vehicle;
import com.example.application.events.VehicleSelectedEvent;
import com.example.application.services.MainService;
import com.example.application.services.UserSettingsService;
import com.example.application.views.MainLayout;

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
			MainLayout layout
	) {
		this.mainService = mainService;
		this.settingsService = settingsService;
		layout.addVehicleSelectedListener(this::onVehicleSelected);

		addClassName("dashboard-view");
		setSizeFull();
		configureGrid();

		add(header, getToolbar(), grid);
		//grid.setSizeFull();

		update();
	}

	private void onVehicleSelected(VehicleSelectedEvent e) {
		update();
	}

	private void configureGrid() {
		grid.addClassNames("dashboard-grid");
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
		toolbar.addClassName("dashboard-toolbar");
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

	private void updateList() {
		List<Tracker> trackers = mainService.findTrackers();
		EventData data = mainService.findLastEventDataForTrackers(trackers);

		grid.setItems(trackers);

		if (settingsService.getDashboardEventFormat().equals(DashboardEventFormat.LAST_SERVICE)) {
			grid.getColumnByKey("date")
					.setHeader("Last date")
					.setRenderer(data.render(data::getDateStatus, data::getLastDate));

			grid.getColumnByKey("mileage")
					.setHeader("Last mileage")
					.setRenderer(data.render(data::getMileageStatus, data::getLastMileage));
		}
		else if (settingsService.getDashboardEventFormat().equals(DashboardEventFormat.NEXT_SERVICE)) {
			grid.getColumnByKey("date")
					.setHeader("Next date")
					.setRenderer(data.render(data::getDateStatus, data::getNextDate));

			grid.getColumnByKey("mileage")
					.setHeader("Next mileage")
					.setRenderer(data.render(data::getMileageStatus, data::getNextMileage));
		}
		else if (settingsService.getDashboardEventFormat().equals(DashboardEventFormat.NEXT_SERVICE_RELATIVE)) {
			grid.getColumnByKey("date")
					.setHeader("Time left")
					.setRenderer(data.render(data::getDateStatus, data::getNextDateRelativeDays, HumanTimeFormatter::formatRounded));

			grid.getColumnByKey("mileage")
					.setHeader("Distance left")
					.setRenderer(data.render(data::getMileageStatus, data::getNextMileageRelative, HumanDistanceFormatter::format));
		}
	}
}
