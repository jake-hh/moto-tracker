package com.example.application.ui.views.dashboard;

import com.example.application.data.DashboardEventFormat;
import com.example.application.data.entity.Tracker;
import com.example.application.services.model.TrackerData;
import com.example.application.services.MainService;
import com.example.application.services.UserSettingsService;
import com.example.application.ui.render.TrackerDataRenderer;
import com.example.application.ui.views.MainLayout;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
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

	private final DashboardHeader header = new DashboardHeader();
	private final Grid<Tracker> grid = new Grid<>(Tracker.class, false);
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

		registerListeners(mainLayout);

		addClassName("view");
		setSizeFull();
		configureGrid();

		add(header, createToolbar(), grid);
		//grid.setSizeFull();

		updateView();
	}

	private void registerListeners(MainLayout mainLayout) {
		mainLayout.addVehicleSelectedListener(e -> updateView());
		mainLayout.addTrackerChangedListener(e -> updateView());
		mainLayout.addEventChangedListener(e -> updateView());
		mainLayout.addOperationChangedListener(e -> updateView());
	}

	private void configureGrid() {
		grid.addClassNames("grid");
		grid.setSizeFull();
		grid.setColumns("name", "interval", "range");

		grid.addColumn(tracker -> "").setKey("date");
		grid.addColumn(tracker -> "").setKey("mileage");

		grid.getColumns().forEach(col -> col.setAutoWidth(true));
	}

	private HorizontalLayout createToolbar() {
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
		updateGrid();
	}

	private void updateView() {
		formatSelect.setValue(settingsService.getDashboardEventFormat());
		updateHeader();
		updateGrid();
	}

	private void updateHeader() {
		header.update(settingsService.getSelectedVehicle());
	}

	private void updateGrid() {
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
}
