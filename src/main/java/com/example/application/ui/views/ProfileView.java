package com.example.application.ui.views;

import com.example.application.data.entity.AppUser;
import com.example.application.security.SecurityService;
import com.example.application.services.MainService;
import com.example.application.ui.views.vehicle.VehicleView;

import com.vaadin.flow.component.Component;
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

		// Vehicle counter
		Span vehicleCounter = new Span("Vehicles: " + mainService.countVehiclesByUser(user));

		// Create edit vehicles button
		var editVehiclesBtn = new Button(LineAwesomeIcon.PEN_SOLID.create());
		editVehiclesBtn.addThemeVariants(
				ButtonVariant.LUMO_ICON,
				ButtonVariant.LUMO_TERTIARY
		);
		editVehiclesBtn.setTooltipText("Edit vehicles");
		editVehiclesBtn.addClickListener(click -> UI.getCurrent().navigate(VehicleView.class));

		// Vehicle bar
		var vehicleBar = new HorizontalLayout(
				vehicleCounter,
				editVehiclesBtn
		);
		vehicleBar.setAlignItems(FlexComponent.Alignment.CENTER);

		// Main column
		VerticalLayout content = new VerticalLayout(
				avatar,
				header,
				vehicleBar,
				createForm()
		);

		content.setAlignItems(FlexComponent.Alignment.CENTER);
		content.setPadding(true);
		content.setSpacing(true);

		return content;
	}

	private Component createForm() {
		var form = new VerticalLayout(
				username,
				firstName,
				lastName,
				email
		);

		form.setWidth("300px");
		form.setAlignItems(FlexComponent.Alignment.STRETCH);
		form.setPadding(false);
		form.setSpacing(true);

		username.setReadOnly(true);
		firstName.setReadOnly(true);
		lastName.setReadOnly(true);
		email.setReadOnly(true);

		return form;
	}
}