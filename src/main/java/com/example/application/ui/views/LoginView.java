package com.example.application.ui.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;


@Route("login") 
@PageTitle("Login | Moto Tracker")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

	private final LoginForm login = new LoginForm(); 

	public LoginView(){
		addClassName("view");
		setSizeFull();
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);

		login.setAction("login");
		login.setForgotPasswordButtonVisible(false);

		var register = new RouterLink("Register Account", RegisterView.class);
		register.addClassName("mt-footer-link");

		add(new H1("Moto Tracker"));
		add(login);
		add(register);
	}

	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		// inform the user about an authentication error
		if(beforeEnterEvent.getLocation()  
		.getQueryParameters()
		.getParameters()
		.containsKey("error")) {
			login.setError(true);
		}
	}
}