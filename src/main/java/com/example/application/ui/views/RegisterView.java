package com.example.application.ui.views;

import com.example.application.services.RegistrationService;
import com.example.application.ui.Notify;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;


@Route("register")
@PageTitle("Register | Moto Tracker")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {

	private final TextField username = new TextField("Username");
	private final PasswordField password = new PasswordField("Password");
	private final PasswordField pConfirm = new PasswordField("Confirm password");

	private final RegistrationService registrationService;


	public RegisterView(RegistrationService registrationService) {
		this.registrationService = registrationService;

		addClassName("view");
		setSizeFull();
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);

		var title = new H1("Moto Tracker");

		var card = new VerticalLayout();
		card.setWidth("344px");           // same width as LoginForm
		card.getStyle().set("padding-top", "var(--lumo-space-xl");
		card.getStyle().set("padding-bottom", "var(--lumo-space-xl");

		var form = new FormLayout();

		var header = new H2("Create account");

		Button register = new Button("Register", this::onRegisterClick);
		register.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		register.setWidthFull();
		register.getStyle().set("margin-top", "var(--lumo-space-l");

		form.add(header, username, password, pConfirm, register);
		card.add(form);
		add(title, card);
	}

	private void onRegisterClick(ClickEvent<Button> click) {

		if (!password.getValue().equals(pConfirm.getValue())) {
			Notify.error("Passwords do not match");
			return;
		}

		try {
			registrationService.register(
					username.getValue(),
					password.getValue()
			);

			Notify.ok("Account created");
			getUI().ifPresent(ui -> ui.navigate("login"));
		}
		catch (Exception e) {
			Notify.error(e.getMessage());
		}
	}
}
