package com.example.application.views.oplist;

import com.example.application.data.Operation;
import com.example.application.services.MainService;
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

import jakarta.annotation.security.PermitAll;
import org.springframework.context.annotation.Scope;


@SpringComponent
@Scope("prototype")
@PermitAll
@Route(value = "operations", layout = MainLayout.class)
@PageTitle("Operations | Moto Tracker")
public class OplistView extends VerticalLayout {
	Grid<Operation> grid = new Grid<>(Operation.class, false);
	// TextField filterText = new TextField();
	OperationForm form;
	MainService service;

	public OplistView(MainService service) {
		this.service = service;
		addClassName("oplist-view");
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
		content.addClassNames("oplist-content");
		content.setSizeFull();
		return content;
	}

	private void configureForm() {
		form = new OperationForm(service.findAllTrackers(), service.findAllEvents());
		form.setWidth("25em");
		form.addSaveListener(this::saveOperation); // <1>
		form.addDeleteListener(this::deleteOperation); // <2>
		form.addCloseListener(e -> closeEditor()); // <3>
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
		editOperation(new Operation());
	}

	private void updateList() {
		grid.setItems(service.findAllOperations(/*filterText.getValue()*/));
	}
}
