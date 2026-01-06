package com.example.application.views.tracker;

import com.example.application.data.Tracker;
import com.example.application.data.Pair;
import com.example.application.events.VehicleSelectedEvent;
import com.example.application.services.MainService;
import com.example.application.views.MainLayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import jakarta.annotation.security.PermitAll;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@SpringComponent
@UIScope
@PermitAll
@Route(value = "", layout = MainLayout.class)
@PageTitle("Trackers | Moto Tracker")
public class TrackerView extends VerticalLayout {

	private final Grid<Tracker> grid = new Grid<>(Tracker.class, false);
	private final TextField filterText = new TextField();
	private final TrackerForm form = new TrackerForm();
	private final MainService service;


	public TrackerView(MainService service, MainLayout layout) {
		this.service = service;
		layout.addVehicleSelectedListener(this::onVehicleSelected);

		addClassName("tracker-view");
		setSizeFull();
		configureGrid();
		configureForm();

		add(getToolbar(), getContent());
		updateList();
		closeEditor();
	}

	private void onVehicleSelected(VehicleSelectedEvent e) {
		updateList();
	}

	private HorizontalLayout getContent() {
		HorizontalLayout content = new HorizontalLayout(grid, form);
		content.setFlexGrow(2, grid);
		content.setFlexGrow(1, form);
		content.addClassNames("tracker-content");
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
		service.saveTracker(event.getTracker());
		updateList();
		closeEditor();
	}

	private void deleteTracker(TrackerForm.DeleteEvent event) {
		service.deleteTracker(event.getTracker());
		updateList();
		closeEditor();
	}

	private void configureGrid() {
		grid.addClassNames("tracker-grid");
		grid.setSizeFull();
		grid.setColumns("name", "interval", "range");

		grid.addColumn(tracker -> "").setHeader("Last date").setKey("lastDate");
		grid.addColumn(tracker -> "").setHeader("Last mileage").setKey("lastMileage");

		grid.getColumns().forEach(col -> col.setAutoWidth(true));

		grid.asSingleSelect().addValueChangeListener(event ->
				editTracker(event.getValue()));
	}

	private Component getToolbar() {
		filterText.setPlaceholder("Filter by name...");
		filterText.setClearButtonVisible(true);
		filterText.setValueChangeMode(ValueChangeMode.LAZY);
		filterText.addValueChangeListener(e -> updateList());

		Button addTrackerButton = new Button("Add tracker");
		addTrackerButton.addClickListener(click -> addTracker());

		var toolbar = new HorizontalLayout(filterText, addTrackerButton);
		toolbar.addClassName("tracker-toolbar");
		return toolbar;
	}

	public void editTracker(Tracker tracker) {
		if (tracker == null) {
			closeEditor();
			return;
		}

		form.setTracker(tracker);
		form.setVisible(true);
		addClassName("tracker-editing");

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
		List<Tracker> trackers = service.findTrackers(filterText.getValue());
		Map<Long, Pair<LocalDate, Integer>> lastEventDataMap = service.findLastEventDataForTrackers(trackers);

		grid.setItems(trackers);

		// Replace column renderer dynamically
		grid.getColumnByKey("lastDate").setRenderer(new TextRenderer<>(t -> {

			Pair<LocalDate, Integer> pair = lastEventDataMap.get(t.getId());
			// System.out.println("is null: " + (pair == null));

			return Optional.ofNullable(pair)
				.map(x -> x.first().toString())
				.orElse("-");
		}));

		// Replace column renderer dynamically
		grid.getColumnByKey("lastMileage").setRenderer(new TextRenderer<>(t -> {

			Pair<LocalDate, Integer> pair = lastEventDataMap.get(t.getId());
			// System.out.println("is null: " + (pair == null));

			return Optional.ofNullable(pair)
				.map(x -> x.second().toString())
				.orElse("-");
		}));
	}
}
