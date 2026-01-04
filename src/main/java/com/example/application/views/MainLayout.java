package com.example.application.views;

import com.example.application.data.Vehicle;
import com.example.application.security.SecurityService;
import com.example.application.services.MainService;
import com.example.application.views.oplist.OplistView;
import com.example.application.views.service.ServiceView;
import com.example.application.views.tracker.TrackerView;
import com.example.application.views.vehicle.VehicleView;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;


public class MainLayout extends AppLayout {
	private final SecurityService securityService;
	private final MainService mainService;
	private final ComboBox<Vehicle> vehicleBox;

	public MainLayout(SecurityService securityService, MainService mainService) {
		this.securityService = securityService;
		this.mainService = mainService;
		this.vehicleBox = new ComboBox<>("Vehicle");

		createHeader();
		createDrawer();
	}

	private void createHeader() {
		H1 logo = new H1("Moto Tracker");
		logo.addClassNames(
			LumoUtility.FontSize.LARGE,
			LumoUtility.Margin.MEDIUM);

		String u = securityService.getAuthenticatedUser().getUsername();
		Button logout = new Button("Log out " + u, e -> securityService.logout()); // <2>

		var header = new HorizontalLayout(
				new DrawerToggle(),
				logo,
				logout
		);

		header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
		header.expand(logo); // <4>
		header.setWidthFull();
		header.addClassNames(
			LumoUtility.Padding.Vertical.NONE,
			LumoUtility.Padding.Horizontal.MEDIUM
		);

		addToNavbar(header); 

	}

	private void createDrawer() {
		// Init Vehicle Box
		vehicleBox.setItems(mainService.findVehicles());
		vehicleBox.setItemLabelGenerator(Vehicle::toStringShort);
		vehicleBox.setWidthFull();

		// Create Edit Vehicles Button
		var editVehiclesBtn = new Button(new Icon(VaadinIcon.EDIT));
		editVehiclesBtn.addThemeVariants(
				ButtonVariant.LUMO_ICON,
				ButtonVariant.LUMO_TERTIARY,
				ButtonVariant.LUMO_WARNING
		);

		editVehiclesBtn.addClickListener(e -> UI.getCurrent().navigate(VehicleView.class));

		// Combine in Vehicle Layout
		var vehicleLayout = new HorizontalLayout(vehicleBox, editVehiclesBtn);
		vehicleLayout.setWidthFull();
		vehicleLayout.setPadding(false);
		vehicleLayout.setSpacing(false);
		vehicleLayout.setAlignItems(FlexComponent.Alignment.END); // optional

		addToDrawer(new VerticalLayout(
				vehicleLayout,
				new RouterLink("Operations", OplistView.class),
				new RouterLink("Services", ServiceView.class),
				new RouterLink("Trackers", TrackerView.class)
		));
	}
}