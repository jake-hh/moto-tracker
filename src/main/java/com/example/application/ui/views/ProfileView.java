package com.example.application.ui.views;

import com.example.application.data.entity.AppUser;
import com.example.application.security.SecurityService;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

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

	private final SecurityService service;


	public ProfileView(SecurityService service) {
		this.service = service;

		addClassName("view");
		setSizeFull();
		setAlignItems(FlexComponent.Alignment.CENTER);
		setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

		binder.bindInstanceFields(this);
		updateView();

		add(createForm());
	}

	private Component createForm() {
		var form = new VerticalLayout(
				username,
				firstName,
				lastName,
				email
		);

		form.setPadding(true);
		form.setSpacing(true);
		form.setWidth("300px");
		form.setAlignItems(FlexComponent.Alignment.STRETCH);

		username.setReadOnly(true);
		firstName.setReadOnly(true);
		lastName.setReadOnly(true);
		email.setReadOnly(true);

		return form;
	}

	private void updateView() {
		binder.setBean(service.getCurrentUser());
	}
}