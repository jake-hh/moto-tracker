package com.example.application.ui.views;

import com.example.application.services.RegistrationService;
//import com.example.application.ui.components.Button;
import com.example.application.ui.Notify;

import com.example.application.ui.dto.RegistrationDTO;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Key;
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
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;


@Route("register")
@PageTitle("Register | Moto Tracker")
@AnonymousAllowed
@SuppressWarnings("FieldCanBeLocal")
public class RegisterView extends VerticalLayout {

	private final TextField username = new TextField("Username");
	private final TextField firstName = new TextField("First name");
	private final TextField lastName = new TextField("Last name");
	private final EmailField email = new EmailField("E-mail address");
	private final PasswordField password = new PasswordField("Password");
	private final PasswordField pConfirm = new PasswordField("Confirm password");

	private final RegistrationService registrationService;

	private final Binder<RegistrationDTO> binder = new BeanValidationBinder<>(RegistrationDTO.class);


	public RegisterView(RegistrationService registrationService) {
		this.registrationService = registrationService;

		addClassName("view");
		setSizeFull();
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);

		var title = new H1("Moto Tracker");

		var card = new VerticalLayout(createRegisterForm());
		card.setWidth("344px");           // same width as LoginForm
		card.getStyle().set("padding-top", "var(--lumo-space-xl");
		card.getStyle().set("padding-bottom", "var(--lumo-space-xl");

		// Wrap in Span if css font stops working
		var loginLink = new RouterLink("Back to login page", LoginView.class);
		loginLink.addClassName("mt-footer-link");

		add(title, card, loginLink);
	}

	public FormLayout createRegisterForm() {
		var header = new H2("Create account");

		username.setValueChangeMode(ValueChangeMode.LAZY);
		firstName.setValueChangeMode(ValueChangeMode.LAZY);
		lastName.setValueChangeMode(ValueChangeMode.LAZY);
		email.setValueChangeMode(ValueChangeMode.LAZY);
		password.setValueChangeMode(ValueChangeMode.LAZY);
		pConfirm.setValueChangeMode(ValueChangeMode.LAZY);

		firstName.setClearButtonVisible(true);
		lastName.setClearButtonVisible(true);

		email.setClearButtonVisible(true);
		email.setPrefixComponent(VaadinIcon.ENVELOPE.create());
		//email.setErrorMessage("Invalid e-mail address");

		password.setPrefixComponent(VaadinIcon.KEY.create());
		pConfirm.setPrefixComponent(VaadinIcon.KEY.create());

		bindFields2AppUser();

		Button registerBtn = new Button("Register", this::validateAndSave);
		registerBtn.addClickShortcut(Key.ENTER);
		registerBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		registerBtn.setWidthFull();
		registerBtn.getStyle().set("margin-top", "var(--lumo-space-l");
		registerBtn.setTooltipText("Invalid input");

		binder.addStatusChangeListener(e ->
				registerBtn.setTooltipText(binder.isValid() ? null : "Invalid input"));

		//binder.addStatusChangeListener(e -> saveBtn.setActive(binder.isValid()));

		return new FormLayout(header, username, firstName, lastName, email, password, pConfirm, registerBtn);
	}

	private void bindFields2AppUser() {
		binder.bindInstanceFields(this);

		binder.forField(username)
				.asRequired("Username is required")
				.withValidator(registrationService::isUsernameAvailable, "Username is already taken")
				.bind("username");   // Use Entity validators

		//binder.forField(email)
				//.withValidator(AppUser::validateEmail, "Invalid e-mail address")
				//.bind("email");

		binder.forField(password)
				.asRequired("Password is required")
				.bind("password");

		binder.forField(pConfirm)
				.asRequired("Please confirm password")
				.withValidator(pc -> pc.equals(password.getValue()), "Passwords do not match")
				.bind("passwordConfirm");
	}

	private void validateAndSave(ClickEvent<Button> click) {
		if (binder.isValid()) { // validates and reads fields
			try {
				RegistrationDTO form = new RegistrationDTO();
				binder.writeBean(form); // copy fields into fresh entity
				registrationService.register(form);
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
