package com.example.application.views.oplist;

import com.example.application.data.Operation;
import com.example.application.events.VehicleSelectedEvent;
import com.example.application.services.MainService;
import com.example.application.services.UserSettingsService;
import com.example.application.views.MainLayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.component.textfield.TextField;
//import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import jakarta.annotation.security.PermitAll;

import java.util.List;


@SpringComponent
@UIScope
@PermitAll
@Route(value = "operations", layout = MainLayout.class)
@PageTitle("Operations | Moto Tracker")
public class OplistView extends VerticalLayout {

	private final MainService mainService;
	private final UserSettingsService settingsService;
	private final Grid<Operation> grid = new Grid<>(Operation.class, false);
	private final OperationForm form = new OperationForm();
	// TextField filterText = new TextField();

	public OplistView(
			MainService mainService,
			UserSettingsService settingsService,
			MainLayout layout
	) {
		this.mainService = mainService;
		this.settingsService = settingsService;
		layout.addVehicleSelectedListener(this::onVehicleSelected);

		addClassName("oplist-view");
		setSizeFull();
		configureGrid();
		configureForm();

		add(getToolbar(), getContent());
		updateList();
		closeEditor();
	}

	private void onVehicleSelected(VehicleSelectedEvent e) {
		updateList();
		updateForm();
	}

	private HorizontalLayout getContent() {
		HorizontalLayout content = new HorizontalLayout(grid, form);
		content.setFlexGrow(2, grid);
		content.setFlexGrow(1, form);
		content.addClassNames("oplist-content");
		content.setSizeFull();
		return content;
	}

	private void configureForm() {
		form.setWidth("25em");
		form.addSaveListener(this::saveOperation); // <1>
		form.addDeleteListener(this::deleteOperation); // <2>
		form.addCloseListener(e -> closeEditor()); // <3>
		updateForm();
	}

	private void saveOperation(OperationForm.SaveEvent event) {
		mainService.saveOperation(event.getOperation());
		updateList();
		closeEditor();
	}

	private void deleteOperation(OperationForm.DeleteEvent event) {
		mainService.deleteOperation(event.getOperation());
		updateList();
		closeEditor();
	}

	private void configureGrid() {
		grid.addClassNames("oplist-grid");
		grid.setSizeFull();
		// grid.setColumns("firstName", "lastName", "email");
		grid.addColumn(operation -> operation.getEvent().getDateStr()).setHeader("Date");
		grid.addColumn(operation -> operation.getEvent().getMileage()).setHeader("Mileage");
		grid.addColumn(operation -> operation.getTracker().getName()).setHeader("Tracker");
		grid.addColumn(operation -> operation.getTracker().getInterval()).setHeader("Interval");
		grid.addColumn(operation -> operation.getTracker().getRange()).setHeader("Range");
		grid.getColumns().forEach(col -> col.setAutoWidth(true));

		grid.asSingleSelect().addValueChangeListener(event ->
				editOperation(event.getValue()));
	}

	private Component getToolbar() {
		// filterText.setPlaceholder("Filter by name...");
		// filterText.setClearButtonVisible(true);
		// filterText.setValueChangeMode(ValueChangeMode.LAZY);
		// filterText.addValueChangeListener(e -> updateList());

		Button addOperationButton = new Button("Add operation");
		addOperationButton.addClickListener(click -> addOperation());

		var toolbar = new HorizontalLayout(/*filterText, */addOperationButton);
		toolbar.addClassName("oplist-toolbar");
		return toolbar;
	}

	public void editOperation(Operation operation) {
		if (operation == null) {
			closeEditor();
		} else {
			form.setOperation(operation);
			form.setVisible(true);
			addClassName("oplist-editing");
		}
	}

	private void closeEditor() {
		form.setOperation(null);
		form.setVisible(false);
		removeClassName("editing");
	}

	private void addOperation() {
		grid.asSingleSelect().clear();

		// TODO: disable add button if no vehicle is present
		if (settingsService.getSelectedVehicle().isPresent())
			editOperation(new Operation());
	}

	private void updateList() {
		grid.setItems(
				settingsService.getSelectedVehicle()
						.map(mainService::findOperationsByVehicle /*filterText.getValue()*/)
						.orElse(List.of())
		);
	}

	private void updateForm() {
		form.setEvents(
				settingsService.getSelectedVehicle()
						.map(mainService::findEventsByVehicle)
						.orElse(List.of())
		);

		form.setTrackers(
				settingsService.getSelectedVehicle()
						.map(mainService::findTrackersByVehicle)
						.orElse(List.of())
		);
	}
}
