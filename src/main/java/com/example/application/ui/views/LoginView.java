package com.example.application.ui.views;

import com.example.application.ui.views.register.RegisterView;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;


@Route("login") 
@PageTitle("Login | Moto Tracker")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

	private final LoginForm loginForm = new LoginForm();


	public LoginView() {
		addClassName("view");
		setSizeFull();
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);

		var title = new H1("Moto Tracker");

		loginForm.setAction("login");
		loginForm.setForgotPasswordButtonVisible(false);

		var registerLink = new RouterLink("Register Account", RegisterView.class);
		registerLink.addClassName("mt-footer-link");

		add(title, loginForm, registerLink);
	}

	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		// inform the user about an authentication error
		if (beforeEnterEvent.getLocation()
				.getQueryParameters()
				.getParameters()
				.containsKey("error")
		) {
			loginForm.setError(true);
		}
	}
}