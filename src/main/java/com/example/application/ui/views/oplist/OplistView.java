package com.example.application.ui.views.oplist;

import com.example.application.data.entity.Operation;
import com.example.application.services.MainService;
import com.example.application.ui.views.MainLayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

	private final MainLayout mainLayout;


	public OplistView(MainService service, MainLayout mainLayout) {
		this.service = service;
		this.mainLayout = mainLayout;

		mainLayout.addVehicleSelectedListener(e -> updateView());
		mainLayout.addTrackerChangedListener(e -> updateView());
		mainLayout.addEventChangedListener(e -> updateView());
		mainLayout.addOperationChangedListener(e -> updateView());

		addClassName("view");
		setSizeFull();
		configureGrid();
		configureForm();

		add(createToolbar(), createContent());
		updateList();
		closeEditor();
	}

	private Component createToolbar() {
		Button addOperationButton = new Button("Add operation");
		addOperationButton.addClickListener(click -> addOperation());

		var toolbar = new HorizontalLayout(addOperationButton);
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
		form.addSaveListener(this::saveOperation);
		form.addDeleteListener(this::deleteOperation);
		form.addCloseListener(e -> closeEditor());
		updateForm();
	}

	private void saveOperation(OperationForm.SaveEvent event) {
		service.saveOperation(event.getValue());
		updateList();
		closeEditor();
		mainLayout.fireTrackerChangedEvent();
	}

	private void deleteOperation(OperationForm.DeleteEvent event) {
		service.deleteOperation(event.getValue());
		updateList();
		closeEditor();
		mainLayout.fireTrackerChangedEvent();
	}

	private void configureGrid() {
		grid.addClassNames("grid");
		grid.setSizeFull();
		grid.addColumn(operation -> operation.getEvent().getDateStr()).setHeader("Date");
		grid.addColumn(operation -> operation.getEvent().getMileage()).setHeader("Mileage");
		grid.addColumn(operation -> operation.getTracker().getName()).setHeader("Tracker");
		grid.addColumn(operation -> operation.getTracker().getInterval()).setHeader("Interval");
		grid.addColumn(operation -> operation.getTracker().getRange()).setHeader("Range");
		grid.getColumns().forEach(col -> col.setAutoWidth(true));

		grid.asSingleSelect().addValueChangeListener(event ->
				editOperation(event.getValue()));
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

	private void updateView() {
		updateList();
		updateForm();
	}

	private void updateList() {
		grid.setItems(service.findOperations());
	}

	private void updateForm() {
		form.setEvents(service.findEvents());
		form.setTrackers(service.findTrackers());
	}
}
