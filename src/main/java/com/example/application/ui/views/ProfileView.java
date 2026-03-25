package com.example.application.ui.views;

import com.example.application.data.entity.AppUser;
import com.example.application.security.SecurityService;
import com.example.application.services.MainService;
import com.example.application.ui.events.VehicleChangedEvent;
import com.example.application.ui.views.vehicle.VehicleView;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.vaadin.lineawesome.LineAwesomeIcon;

import jakarta.annotation.security.PermitAll;


@SpringComponent
@UIScope
@PermitAll
@Route(value = "profile", layout = MainLayout.class)
@PageTitle("Profile | Moto Tracker")
public class ProfileView extends VerticalLayout {

	private final TextField username = new TextField("username");
	private final TextField firstName = new TextField("firstName");
	private final TextField lastName = new TextField("lastName");
	private final TextField email = new TextField("email");

	private final Binder<AppUser> binder = new Binder<>(AppUser.class);
	private final MainService mainService;
	private final SecurityService securityService;


	public ProfileView(MainService mainService, SecurityService securityService) {
		this.mainService = mainService;
		this.securityService = securityService;

		addClassName("view");
		setSizeFull();
		setAlignItems(FlexComponent.Alignment.CENTER);
		setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

		ComponentUtil.addListener(UI.getCurrent(), VehicleChangedEvent.class, e -> updateView());

		binder.bindInstanceFields(this);
		updateView();
	}

	private void updateView() {
		binder.setBean(securityService.getCurrentUser());
		removeAll();
		add(createContent());
	}

	private Component createContent() {
		AppUser user = securityService.getCurrentUser();

		// Avatar
		Avatar avatar = new Avatar();
		avatar.setWidth("100px");
		avatar.setHeight("100px");

		// Username header
		H4 header = new H4(user.getUsername());

		// Main column
		VerticalLayout content = new VerticalLayout(
				avatar,
				header,
				createVehiclesBar(user)
		);

		if (mainService.countVehiclesByUser(user) == 0) {
			Span hint = new Span("Add your first vehicle to start tracking maintenance.");
			hint.addClassNames("mt-helper-text", "warning");
			hint.getStyle().set("margin", "0 var(--lumo-space-m) var(--lumo-space-m)");
			content.add(hint);
		}

		content.add(createForm());

		content.setAlignItems(FlexComponent.Alignment.CENTER);
		content.setWidth("340px");
		content.setPadding(true);
		content.setSpacing(true);

		return content;
	}

	private Component createVehiclesBar(AppUser user) {
		// Vehicle counter
		Span counter = new Span("Vehicles: " + mainService.countVehiclesByUser(user));

		// Edit vehicles button
		var editBtn = new Button(LineAwesomeIcon.PEN_SOLID.create());
		editBtn.addThemeVariants(
				ButtonVariant.LUMO_ICON,
				ButtonVariant.LUMO_TERTIARY
		);
		editBtn.setTooltipText("Edit vehicles");
		editBtn.addClickListener(click -> UI.getCurrent().navigate(VehicleView.class));

		// Add vehicle button
		var addBtn = new Button(LineAwesomeIcon.PLUS_SOLID.create());
		addBtn.addThemeVariants(
				ButtonVariant.LUMO_ICON,
				ButtonVariant.LUMO_TERTIARY
		);
		addBtn.setTooltipText("Add vehicle");
		addBtn.addClickListener(click -> UI.getCurrent().navigate("vehicles/new"));

		// Buttons toolbar
		var buttons = new HorizontalLayout(editBtn, addBtn);
		buttons.setSpacing(false);

		// Vehicle bar
		var vehiclesBar = new HorizontalLayout(counter, buttons);
		vehiclesBar.addClassName("mt-border");
		vehiclesBar.setWidthFull();
		vehiclesBar.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
		vehiclesBar.setAlignItems(FlexComponent.Alignment.CENTER);
		vehiclesBar.getStyle().set("padding", "var(--lumo-space-xs) var(--lumo-space-m)");

		return vehiclesBar;
	}

	private Component createForm() {
		var form = new VerticalLayout(
				username,
				firstName,
				lastName,
				email
		);

		form.addClassName("mt-border");
		form.setWidthFull();
		form.setAlignItems(FlexComponent.Alignment.STRETCH);
		form.getStyle().set("padding", "0 var(--lumo-space-m) var(--lumo-space-m)");
		form.setSpacing(true);

		username.setReadOnly(true);
		firstName.setReadOnly(true);
		lastName.setReadOnly(true);
		email.setReadOnly(true);

		return form;
	}
}