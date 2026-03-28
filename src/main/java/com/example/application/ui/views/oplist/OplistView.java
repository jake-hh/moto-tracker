package com.example.application.ui.views.oplist;

import com.example.application.data.BasicInterval;
import com.example.application.data.entity.Operation;
import com.example.application.services.MainService;
import com.example.application.ui.events.*;
import com.example.application.ui.views.MainLayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSingleSelectionModel;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
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
public class OplistView extends VerticalLayout implements BeforeEnterObserver {

	private final Grid<Operation> grid = new Grid<>(Operation.class, false);
	private final OperationForm form = new OperationForm();

	private final MainService service;


	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		if (service.findVehicles().isEmpty())
			event.forwardTo("profile");
	}

	public OplistView(MainService service) {
		this.service = service;

		ComponentUtil.addListener(UI.getCurrent(), VehicleSelectedEvent.class, e -> updateView());
		ComponentUtil.addListener(UI.getCurrent(), TrackerChangedEvent.class, e -> updateView());
		ComponentUtil.addListener(UI.getCurrent(), EventChangedEvent.class, e -> updateView());
		ComponentUtil.addListener(UI.getCurrent(), OperationChangedEvent.class, e -> updateView());

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
		var gridCol = new VerticalLayout(grid);
		gridCol.setSizeFull();
		gridCol.setPadding(false);
		gridCol.setSpacing(false);

		var content = new HorizontalLayout(gridCol, form);
		content.setFlexGrow(2, gridCol);
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
		closeEditor();
		ComponentUtil.fireEvent(UI.getCurrent(), new OperationChangedEvent(UI.getCurrent(), false));
	}

	private void deleteOperation(OperationForm.DeleteEvent event) {
		service.deleteOperation(event.getValue());
		closeEditor();
		ComponentUtil.fireEvent(UI.getCurrent(), new OperationChangedEvent(UI.getCurrent(), false));
	}

	private void configureGrid() {
		grid.addClassNames("grid");
		grid.setSizeFull();

		grid.addColumn(op -> op.getEvent().getDateStr()).setHeader("Date");
		grid.addColumn(op -> op.getEvent().getMileage()).setHeader("Mileage");
		grid.addColumn(op -> op.getTracker().getName()).setHeader("Tracker");
		grid.addColumn(op -> op.getTracker().getInterval()).setHeader("Interval")
				.setComparator(op -> BasicInterval.toDays(op.getTracker().getInterval()));

		grid.addColumn(op -> op.getTracker().getRange()).setHeader("Range");

		grid.getColumns().forEach(col -> {
			col.setAutoWidth(true);
			col.setSortable(true);
		});

		((GridSingleSelectionModel<Operation>) grid.getSelectionModel()).setDeselectAllowed(false);

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
		grid.asSingleSelect().clear();
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
		var items = service.findOperations();
		grid.setItems(items);
	}

	private void updateForm() {
		form.setEvents(service.findEvents());
		form.setTrackers(service.findTrackers());
	}
}
