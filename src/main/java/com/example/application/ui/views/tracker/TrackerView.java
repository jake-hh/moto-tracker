package com.example.application.ui.views.tracker;

import com.example.application.data.DashboardEventFormat;
import com.example.application.data.entity.Tracker;
import com.example.application.services.MainService;
import com.example.application.services.model.TrackerData;
import com.example.application.ui.render.TrackerDataRenderer;
import com.example.application.ui.views.MainLayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import jakarta.annotation.security.PermitAll;

import java.util.List;


@SpringComponent
@UIScope
@PermitAll
@Route(value = "trackers", layout = MainLayout.class)
@PageTitle("Trackers | Moto Tracker")
public class TrackerView extends VerticalLayout {

	private final Grid<Tracker> grid = new Grid<>(Tracker.class, false);
	private final TextField filterText = new TextField();
	private final TrackerForm form = new TrackerForm();

	private final MainService service;
	private final MainLayout mainLayout;


	public TrackerView(MainService service, MainLayout mainLayout) {
		this.service = service;
		this.mainLayout = mainLayout;

		mainLayout.addVehicleSelectedListener(e -> updateList());
		mainLayout.addEventChangedListener(e -> updateList());
		mainLayout.addOperationChangedListener(e -> updateList());

		addClassName("view");
		setSizeFull();
		configureGrid();
		configureForm();

		add(createToolbar(), createContent());
		updateList();
		closeEditor();
	}

	private Component createToolbar() {
		filterText.setPlaceholder("Filter by name...");
		filterText.setClearButtonVisible(true);
		filterText.setValueChangeMode(ValueChangeMode.LAZY);
		filterText.addValueChangeListener(e -> updateList());

		Button addTrackerButton = new Button("Add tracker");
		addTrackerButton.addClickListener(click -> addTracker());

		var toolbar = new HorizontalLayout(filterText, addTrackerButton);
		toolbar.addClassName("toolbar");
		return toolbar;
	}

	private Component createContent() {
		var content = new HorizontalLayout(grid, form);
		content.setFlexGrow(2, grid);
		content.setFlexGrow(1, form);
		content.addClassNames("content");
		content.setSizeFull();
		return content;
	}

	private void configureForm() {
		form.setWidth("25em");
		form.addSaveListener(this::saveTracker);
		form.addDeleteListener(this::deleteTracker);
		form.addCloseListener(e -> closeEditor());
	}

	private void saveTracker(TrackerForm.SaveEvent event) {
		service.saveTracker(event.getValue());
		updateList();
		closeEditor();
		mainLayout.fireTrackerChangedEvent();
	}

	private void deleteTracker(TrackerForm.DeleteEvent event) {
		service.deleteTracker(event.getValue());
		updateList();
		closeEditor();
		mainLayout.fireTrackerChangedEvent();
	}

	private void configureGrid() {
		grid.addClassNames("grid");
		grid.setSizeFull();
		grid.setColumns("name", "interval", "range");

		grid.addColumn(tracker -> "").setHeader("Last date").setKey("date");
		grid.addColumn(tracker -> "").setHeader("Last mileage").setKey("mileage");

		grid.getColumns().forEach(col -> col.setAutoWidth(true));

		grid.asSingleSelect().addValueChangeListener(event ->
				editTracker(event.getValue()));
	}

	public void editTracker(Tracker tracker) {
		if (tracker == null) {
			closeEditor();
			return;
		}

		form.setTracker(tracker);
		form.setVisible(true);
		addClassName("editing");

		boolean used = tracker.getId() != null && service.isTrackerUsed(tracker);
		form.setDeleteEnabled(!used);
	}

	private void closeEditor() {
		form.setTracker(null);
		form.setVisible(false);
		removeClassName("editing");
	}

	private void addTracker() {
		grid.asSingleSelect().clear();

		// TODO: disable add button if no vehicle is present
		service.createTracker().ifPresent(this::editTracker);
	}

	private void updateList() {
		List<Tracker> trackers = service.findTrackers();
		TrackerData data = service.loadDataForTrackers(trackers);

		grid.setItems(trackers);

		grid.getColumnByKey("date")
				.setRenderer(TrackerDataRenderer.renderDate(data, DashboardEventFormat.LAST_SERVICE));

		grid.getColumnByKey("mileage")
				.setRenderer(TrackerDataRenderer.renderMileage(data, DashboardEventFormat.LAST_SERVICE));
	}
}
