package com.example.application.ui.views.dashboard;

import com.example.application.data.BasicInterval;
import com.example.application.data.DashboardEventFormat;
import com.example.application.data.entity.Tracker;
import com.example.application.services.model.TrackerData;
import com.example.application.services.MainService;
import com.example.application.services.UserSettingsService;
import com.example.application.ui.render.TrackerDataComparator;
import com.example.application.ui.render.TrackerDataRenderer;
import com.example.application.ui.views.MainLayout;

import com.vaadin.flow.component.Component;
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

		mainLayout.addVehicleSelectedListener(e -> updateView());
		mainLayout.addTrackerChangedListener(e -> updateView());
		mainLayout.addEventChangedListener(e -> updateView());
		mainLayout.addOperationChangedListener(e -> updateView());

		addClassName("view");
		setSizeFull();
		configureGrid();

		add(header, createToolbar(), grid);
		//grid.setSizeFull();

		updateView();
	}

	private Component createToolbar() {
		formatSelect.setLabel("Service format");
		formatSelect.setItems(DashboardEventFormat.values());
		formatSelect.setItemLabelGenerator(DashboardEventFormat::getLabel);
		formatSelect.addValueChangeListener(this::saveFormatAndUpdate);

		var toolbar = new HorizontalLayout(formatSelect);
		toolbar.addClassName("toolbar");
		return toolbar;
	}

	private void configureGrid() {
		grid.addClassNames("grid");
		grid.setSizeFull();

		grid.addColumn("name");

		grid.addColumn("interval")
				.setSortable(true)
				.setComparator(t -> BasicInterval.toDays(t.getInterval()));

		grid.addColumn("range");

		grid.addColumn(t -> "")
				.setKey("date")
				.setSortable(true);

		grid.addColumn(t -> "")
				.setKey("mileage")
				.setSortable(true);

		grid.getColumns().forEach(col -> col.setAutoWidth(true));
	}

	private void saveFormatAndUpdate(ValueChangeEvent<DashboardEventFormat> change) {
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
				.setRenderer(TrackerDataRenderer.renderDate(data, format))
				.setComparator(t -> TrackerDataComparator.compareDate(data, format, t));

		grid.getColumnByKey("mileage")
				.setHeader(getMileageColumnHeader(format))
				.setRenderer(TrackerDataRenderer.renderMileage(data, format))
				.setComparator(t -> TrackerDataComparator.compareMileage(data, format, t));

		// FIXME: Grid doesn't sort after changing type with radioButton - refactor this whole View
		grid.getDataProvider().refreshAll();
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
