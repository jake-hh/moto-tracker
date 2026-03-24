package com.example.application.ui.views;

import com.example.application.data.entity.AppUser;
import com.example.application.data.entity.Vehicle;
import com.example.application.security.SecurityService;
import com.example.application.services.MainService;
import com.example.application.services.UserSettingsService;
import com.example.application.ui.events.VehicleChangedEvent;
import com.example.application.ui.events.VehicleSelectedEvent;
import com.example.application.ui.render.VehicleIconRenderer;
import com.example.application.ui.views.dashboard.DashboardView;
import com.example.application.ui.views.oplist.OplistView;
import com.example.application.ui.views.service.ServiceView;
import com.example.application.ui.views.tracker.TrackerView;
import com.example.application.ui.views.vehicle.VehicleView;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverPosition;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIcon;


@SpringComponent
@UIScope
public class MainLayout extends AppLayout {

	private final ComboBox<Vehicle> vehicleBox = new ComboBox<>("Vehicle");

	private final MainService mainService;
	private final SecurityService securityService;
	private final UserSettingsService settingsService;


	public MainLayout(
			MainService mainService,
			SecurityService securityService,
			UserSettingsService settingsService
	) {
		this.securityService = securityService;
		this.settingsService = settingsService;
		this.mainService = mainService;

		ComponentUtil.addListener(UI.getCurrent(), VehicleChangedEvent.class, e -> updateVehicleBox());

		addNavbar();
		addDrawer();
	}

	private void addNavbar() {
		H1 logo = new H1("Moto Tracker");
		logo.addClassNames(
				LumoUtility.FontSize.LARGE,
				LumoUtility.Margin.MEDIUM
		);

		var navbar = new HorizontalLayout(new DrawerToggle(), logo, createAvatar());
		navbar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
		navbar.expand(logo);
		navbar.setWidthFull();
		navbar.addClassNames(
				LumoUtility.Padding.Vertical.NONE,
				LumoUtility.Padding.Horizontal.MEDIUM
		);
		addToNavbar(navbar);
	}

	private Component createAvatar() {
		Avatar avatar = new Avatar();
		avatar.addClassName("avatar");
		avatar.getElement().setAttribute("tabindex", "-1");

		Button button = new Button(avatar);
		button.addClassName("avatar-button");
		button.addThemeVariants(
				ButtonVariant.LUMO_ICON,
				ButtonVariant.LUMO_TERTIARY_INLINE
		);

		Popover popover = createPopover();
		popover.setTarget(button);

		return new HorizontalLayout(button, popover);
	}

	private Popover createPopover() {
		var popover = new Popover();
		popover.setModal(true);
		popover.setOverlayRole("menu");
		popover.setAriaLabel("User menu");
		popover.setPosition(PopoverPosition.BOTTOM_END);

		var header = new HorizontalLayout();
		header.addClassName("popover-header");

		Avatar popoverAvatar = new Avatar();
		popoverAvatar.getElement().setAttribute("tabindex", "-1");
		popoverAvatar.addThemeVariants(AvatarVariant.LUMO_LARGE);

		header.add(popoverAvatar, createNameLayout());

		var menu = new VerticalLayout();
		menu.addClassName("popover-links");

		Button profileBtn = createMenuLink(
				"User profile",
				() -> getUI().ifPresent(ui -> {
						ui.navigate("profile");
						popover.close();
				})
		);

		Button logoutBtn = createMenuLink(
				"Log out",
				securityService::logout
		);

		menu.add(profileBtn, logoutBtn);

		popover.add(header, menu);

		return popover;
	}

	private VerticalLayout createNameLayout() {
		AppUser user = securityService.getCurrentUser();
		String username = user.getUsername();
		String firstName = user.getFirstName();
		String lastName = user.getLastName();

		var nameLayout = new VerticalLayout();
		nameLayout.setSpacing(false);
		nameLayout.setPadding(false);
		nameLayout.addClassName("popover-header-name-layout");

		if (firstName != null || lastName != null) {
			String fullName;

			if (firstName != null)
				if (lastName != null)
					fullName = firstName + " " + lastName;
				else
					fullName = firstName;
			else
				fullName = lastName;

			Span title = new Span(fullName);
			title.addClassName("popover-header-title");
			nameLayout.add(title);
		}

		Span subtitle = new Span(username);
		subtitle.addClassName("popover-header-subtitle");
		nameLayout.add(subtitle);

		return nameLayout;
	}

	private Button createMenuLink(String text, Runnable onClick) {
		Button button = new Button(text);
		button.addClickListener(click -> onClick.run());
		button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		button.addClassName("popover-link");
		return button;
	}

	private void addDrawer() {
		// Init Vehicle Box
		vehicleBox.setItemLabelGenerator(Vehicle::toStringShort);
		vehicleBox.setWidthFull();
		vehicleBox.addValueChangeListener(this::saveSelectedVehicle);
		vehicleBox.setRenderer(new ComponentRenderer<>(VehicleIconRenderer::getDropdownItemsByVehicle));
		updateVehicleBox();

		// Create Edit Vehicles Button
		var editVehiclesBtn = new Button(LineAwesomeIcon.PEN_SOLID.create());
		editVehiclesBtn.addThemeVariants(
				ButtonVariant.LUMO_ICON,
				ButtonVariant.LUMO_TERTIARY
		);
		editVehiclesBtn.setTooltipText("Edit vehicles");

		editVehiclesBtn.addClickListener(click -> UI.getCurrent().navigate(VehicleView.class));

		// Combine in Vehicle Layout
		var vehicleBar = new HorizontalLayout(vehicleBox, editVehiclesBtn);
		vehicleBar.setWidthFull();
		vehicleBar.setPadding(false);
		vehicleBar.setSpacing(false);
		vehicleBar.setAlignItems(FlexComponent.Alignment.END);

		addToDrawer(new VerticalLayout(
				vehicleBar,
				new RouterLink("Dashboard", DashboardView.class),
				new RouterLink("Operations", OplistView.class),
				new RouterLink("Services", ServiceView.class),
				new RouterLink("Trackers", TrackerView.class)
		));
	}

	private void saveSelectedVehicle(ValueChangeEvent<Vehicle> change) {
		if (!change.isFromClient()) return;

		Vehicle vehicle = change.getValue();

		settingsService.updateSelectedVehicle(vehicle);
		updateVehicleBoxPrefixIcon(vehicle);
		ComponentUtil.fireEvent(UI.getCurrent(), new VehicleSelectedEvent(UI.getCurrent(), vehicle));
	}

	public void updateVehicleBox() {
		vehicleBox.setItems(mainService.findVehicles());
		settingsService.getSelectedVehicle().ifPresent(vehicle -> {
			vehicleBox.setValue(vehicle);
			updateVehicleBoxPrefixIcon(vehicle);
		});
	}

	public void updateVehicleBoxPrefixIcon(Vehicle vehicle) {
		var icon = VehicleIconRenderer.getSelectedVehicleIconByVehicle(vehicle);
		vehicleBox.setPrefixComponent(icon);
	}

}