package com.example.application.views.list;

import com.example.application.data.Tracker;
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

import jakarta.annotation.security.PermitAll;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@SpringComponent
@Scope("prototype")
@PermitAll
@Route(value = "trackers", layout = MainLayout.class)
@PageTitle("Trackers | Moto Tracker")
public class TrackerView extends VerticalLayout {

	Grid<Tracker> grid = new Grid<>(Tracker.class, false);
	TextField filterText = new TextField();
	TrackerForm form;
	MainService service;


	public TrackerView(MainService service) {
		this.service = service;
		addClassName("tracker-view");
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
		content.addClassNames("tracker-content");
		content.setSizeFull();
		return content;
	}

	private void configureForm() {
		form = new TrackerForm();
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
		} else {
			form.setTracker(tracker);
			form.setVisible(true);
			addClassName("tracker-editing");
		}
	}

	private void closeEditor() {
		form.setTracker(null);
		form.setVisible(false);
		removeClassName("editing");
	}

	private void addTracker() {
		grid.asSingleSelect().clear();
		editTracker(new Tracker());
	}

	private void updateList() {
		List<Tracker> trackers = service.findAllTrackers(filterText.getValue());
		Map<Long, Integer> lastMileageMap = service.findLastMileagesForTrackers(trackers);

		grid.setItems(trackers);

		// Replace column renderer dynamically
		grid.getColumnByKey("lastMileage").setRenderer(new TextRenderer<>(t ->
				Optional.ofNullable(lastMileageMap.get(t.getId()))
						.map(Object::toString)
						.orElse("-")
		));
	}

	// private void updateList() {
	// 	grid.setItems(service.findAllTrackers(filterText.getValue()));
	// }
}
