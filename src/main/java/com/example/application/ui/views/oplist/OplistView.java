package com.example.application.ui.views.oplist;

import com.example.application.data.entity.Operation;
import com.example.application.services.MainService;
import com.example.application.ui.events.VehicleSelectedEvent;
import com.example.application.ui.views.MainLayout;

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


@SpringComponent
@UIScope
@PermitAll
@Route(value = "operations", layout = MainLayout.class)
@PageTitle("Operations | Moto Tracker")
public class OplistView extends VerticalLayout {

	private final MainService service;
	private final Grid<Operation> grid = new Grid<>(Operation.class, false);
	private final OperationForm form = new OperationForm();
	// TextField filterText = new TextField();

	public OplistView(MainService service, MainLayout layout) {
		this.service = service;
		layout.addVehicleSelectedListener(this::onVehicleSelected);

		addClassName("view");
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
		content.addClassNames("content");
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
		service.saveOperation(event.getOperation());
		updateList();
		closeEditor();
	}

	private void deleteOperation(OperationForm.DeleteEvent event) {
		service.deleteOperation(event.getOperation());
		updateList();
		closeEditor();
	}

	private void configureGrid() {
		grid.addClassNames("grid");
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
		toolbar.addClassName("toolbar");
		return toolbar;
	}

	public void editOperation(Operation operation) {
		if (operation == null) {
			closeEditor();
		} else {
			form.setOperation(operation);
			form.setVisible(true);
			addClassName("editing");
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
		service.createOperation().ifPresent(this::editOperation);
	}

	private void updateList() {
		grid.setItems(service.findOperations(/*filterText.getValue()*/));
	}

	private void updateForm() {
		form.setEvents(service.findEvents());
		form.setTrackers(service.findTrackers());
	}
}
