package com.example.application.ui.views.tracker;

import com.example.application.data.BasicInterval;
import com.example.application.data.DashboardEventFormat;
import com.example.application.data.entity.DefaultTracker;
import com.example.application.data.entity.Tracker;
import com.example.application.services.MainService;
import com.example.application.services.model.TrackerData;
import com.example.application.ui.events.*;
import com.example.application.ui.render.TrackerDataComparator;
import com.example.application.ui.render.TrackerDataRenderer;
import com.example.application.ui.views.MainLayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
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
public class TrackerView extends VerticalLayout implements BeforeEnterObserver {

	private final Grid<Tracker> grid = new Grid<>(Tracker.class, false);
	private final Grid<DefaultTracker> defaultGrid = new Grid<>(DefaultTracker.class, false);
	private final Span emptyLabel = new Span("No trackers");
	private final TextField filterText = new TextField();
	private final TrackerForm form = new TrackerForm();

	private final MainService service;


	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		if (service.findVehicles().isEmpty())
			event.forwardTo("profile");
	}

	public TrackerView(MainService service) {
		this.service = service;

		ComponentUtil.addListener(UI.getCurrent(), VehicleSelectedEvent.class, e -> { updateList(); updateDefaultList(); });
		ComponentUtil.addListener(UI.getCurrent(), TrackerChangedEvent.class, e -> { updateList(); updateDefaultList(); });
		ComponentUtil.addListener(UI.getCurrent(), EventChangedEvent.class, e -> updateList());
		ComponentUtil.addListener(UI.getCurrent(), OperationChangedEvent.class, e -> updateList());

		addClassName("view");
		setSizeFull();
		configureGrid();
		configureDefaultGrid();
		configureForm();

		add(createToolbar(), createContent());
		updateList();
		updateDefaultList();
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
		var heading = new H3("Suggested trackers");
		heading.getStyle()
				.set("margin-top", "var(--lumo-space-xl)")
				.set("margin-bottom", "var(--lumo-space-m)");

		var spacer = new Span(".");
		spacer.getStyle()
				.set("margin-top", "var(--lumo-space-xl)")
				.set("color", "white");

		emptyLabel.addClassNames("mt-helper-text", "warning");

		var grids = new VerticalLayout(grid, emptyLabel, heading, defaultGrid, spacer);
		grids.setPadding(false);
		grids.setSpacing(false);

		var content = new HorizontalLayout(grids, form);
		content.setFlexGrow(2, grids);
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
		closeEditor();
		ComponentUtil.fireEvent(UI.getCurrent(), new TrackerChangedEvent(UI.getCurrent()));
	}

	private void deleteTracker(TrackerForm.DeleteEvent event) {
		service.deleteTracker(event.getValue());
		closeEditor();
		ComponentUtil.fireEvent(UI.getCurrent(), new TrackerChangedEvent(UI.getCurrent()));
	}

	private void configureGrid() {
		grid.addClassNames("grid");
		grid.setAllRowsVisible(true);

		grid.addColumn("name");

		grid.addColumn("interval")
				.setSortable(true)
				.setComparator(t -> BasicInterval.toDays(t.getInterval()));

		grid.addColumn("range");

		grid.addColumn(t -> "")
				.setKey("date")
				.setHeader("Last date")
				.setSortable(true);

		grid.addColumn(t -> "")
				.setKey("mileage")
				.setHeader("Last mileage")
				.setSortable(true);

		grid.getColumns().forEach(col -> col.setAutoWidth(true));

		grid.asSingleSelect().addValueChangeListener(event -> {
			Tracker selected = event.getValue();
			if (selected == null) return;

			defaultGrid.asSingleSelect().clear();
			editTracker(selected);
		});
	}

	private void configureDefaultGrid() {
		defaultGrid.addClassNames("grid");
		defaultGrid.setAllRowsVisible(true);

		defaultGrid.addColumn("name");
		defaultGrid.addColumn("interval")
				.setSortable(true)
				.setComparator(t -> BasicInterval.toDays(t.getInterval()));
		defaultGrid.addColumn("range");

		defaultGrid.getColumns().forEach(col -> col.setAutoWidth(true));

		defaultGrid.asSingleSelect().addValueChangeListener(event -> {
			DefaultTracker selected = event.getValue();
			if (selected == null) return;

			grid.asSingleSelect().clear();
			service.createTracker().ifPresent(t -> {
				selected.passValues(t);
				editTracker(t);
			});
		});
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
		defaultGrid.asSingleSelect().clear();

		// TODO: disable add button if no vehicle is present
		service.createTracker().ifPresent(this::editTracker);
	}

	private void updateList() {
		List<Tracker> trackers = service.findTrackers(filterText.getValue());
		TrackerData data = service.loadDataForTrackers(trackers);

		boolean empty = trackers.isEmpty();
		grid.setVisible(!empty);
		emptyLabel.setVisible(empty);
		grid.setItems(trackers);

		grid.getColumnByKey("date")
				.setRenderer(TrackerDataRenderer.renderDate(data, DashboardEventFormat.LAST_SERVICE))
				.setComparator(t -> TrackerDataComparator.compareDate(data, DashboardEventFormat.LAST_SERVICE, t));

		grid.getColumnByKey("mileage")
				.setRenderer(TrackerDataRenderer.renderMileage(data, DashboardEventFormat.LAST_SERVICE))
				.setComparator(t -> TrackerDataComparator.compareMileage(data, DashboardEventFormat.LAST_SERVICE, t));
	}

	private void updateDefaultList() {
		defaultGrid.setItems(service.findDefaultTrackers());
	}
}
