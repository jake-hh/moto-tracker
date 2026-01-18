package com.example.application.ui.views;

import com.example.application.data.AppUserPolicy;
import com.example.application.data.entity.AppUser;
import com.example.application.services.RegistrationService;
import com.example.application.ui.Notify;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;


@Route("register")
@PageTitle("Register | Moto Tracker")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {

	private final TextField username = new TextField("Username");
	private final TextField firstName = new TextField("First name");
	private final TextField lastName = new TextField("First name");
	private final EmailField email = new EmailField("E-mail address");
	private final PasswordField password = new PasswordField("Password");
	private final PasswordField pConfirm = new PasswordField("Confirm password");

	private final RegistrationService registrationService;
	private final Binder<AppUser> binder = new Binder<>(AppUser.class);


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

		firstName.setClearButtonVisible(true);
		lastName.setClearButtonVisible(true);

		email.setClearButtonVisible(true);
		email.setPrefixComponent(VaadinIcon.ENVELOPE.create());

		password.setPrefixComponent(VaadinIcon.KEY.create());
		pConfirm.setPrefixComponent(VaadinIcon.KEY.create());

		bindFields2AppUser();

		Button register = new Button("Register", this::validateAndSave);
		register.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		register.setWidthFull();
		register.getStyle().set("margin-top", "var(--lumo-space-l");

		var loginLink = new RouterLink("Back to login page", LoginView.class);
		loginLink.addClassName("mt-footer-link");

		form.add(header, username, firstName, lastName, email, password, pConfirm, register);
		card.add(form);
		add(title, card, loginLink);
	}

	private void bindFields2AppUser() {
		binder.forField(username)
				.asRequired("Username is required")
				.withValidator(AppUserPolicy::isValidUsername, "Username must contain lowercase alphanumeric or dash and begin with a lowercase letter")
				.withValidator(AppUserPolicy::isUsernameLongEnough, "Username is too short")
				.withValidator(AppUserPolicy::isEmptyOrNotTooLong, "Username is too long")
				.withValidator(registrationService::isUsernameAvailable, "Username is already taken")
				.bind(AppUser::getUsername, AppUser::setUsername);

		binder.forField(firstName)
				.withValidator(AppUserPolicy::isAlphabetic, "Name must be alphabetic")
				.withValidator(AppUserPolicy::isNameEmptyOrLongEnough, "Name is too short")
				.withValidator(AppUserPolicy::isEmptyOrNotTooLong, "Name is too long")
				.bind(AppUser::getFirstName, AppUser::setFirstName);

		binder.forField(lastName)
				.withValidator(AppUserPolicy::isAlphabetic, "Name must be alphabetic")
				.withValidator(AppUserPolicy::isNameEmptyOrLongEnough, "Name is too short")
				.withValidator(AppUserPolicy::isEmptyOrNotTooLong, "Name is too long")
				.bind(AppUser::getLastName, AppUser::setLastName);

		binder.forField(email)
				.withValidator(AppUserPolicy::isEmailEmptyOrValid, "Enter a valid e-mail address")
				.withValidator(AppUserPolicy::isEmptyOrNotTooLong, "E-mail is too long")
				.bind(AppUser::getEmail, AppUser::setEmail);

		binder.forField(password)
				.asRequired("Password is required")
				.withValidator(AppUserPolicy::isPasswordLongEnough, "Password is too short")
				.withValidator(AppUserPolicy::hasUppercase, "Password must contain uppercase")
				.withValidator(AppUserPolicy::hasLowercase, "Password must contain lowercase")
				.withValidator(AppUserPolicy::hasDigit, "Password must contain digit")
				.withValidator(AppUserPolicy::isEmptyOrNotTooLong, "Password is too long")
				.bind(AppUser::getPasswordHash, AppUser::setPasswordHash);

		binder.forField(pConfirm)
				.asRequired("Please confirm password")
				.withValidator(pc -> password.getValue().equals(pc), "Passwords do not match")
				.bind(u -> "", (u, v) -> {});
	}

	private void validateAndSave(ClickEvent<Button> click) {
		var user = new AppUser();

		if (binder.writeBeanIfValid(user)) { // validates and reads fields
			try {
				registrationService.register(user);
				getUI().ifPresent(ui -> ui.navigate("login"));
			}
			catch (Exception e) {
				Notify.error(e.getMessage());
			}
		}
		else {
			binder.validate(); // This triggers the fields to show errors
		}
	}
}
