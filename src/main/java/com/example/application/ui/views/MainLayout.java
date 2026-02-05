package com.example.application.ui.views;

import com.example.application.data.entity.Vehicle;
import com.example.application.security.SecurityService;
import com.example.application.services.MainService;
import com.example.application.services.UserSettingsService;
import com.example.application.ui.events.*;
import com.example.application.ui.views.dashboard.DashboardView;
import com.example.application.ui.views.oplist.OplistView;
import com.example.application.ui.views.service.ServiceView;
import com.example.application.ui.views.tracker.TrackerView;
import com.example.application.ui.views.vehicle.VehicleView;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
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
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;


@SpringComponent
@UIScope
public class MainLayout extends AppLayout {
	private final SecurityService securityService;
	private final UserSettingsService settingsService;
	private final MainService mainService;
	private final ComboBox<Vehicle> vehicleBox = new ComboBox<>("Vehicle");

	public MainLayout(
			SecurityService securityService,
			UserSettingsService settingsService,
			MainService mainService,
			VehicleView vehicleView
	) {
		this.securityService = securityService;
		this.settingsService = settingsService;
		this.mainService = mainService;

		vehicleView.addVehicleChangedListener(this::onVehicleChanged);

		createHeader();
		createDrawer();
	}

	private void onVehicleChanged(VehicleChangedEvent change) {
		refreshVehicleBox();
	}

	private void onVehicleBoxChange(ValueChangeEvent<Vehicle> change) {
		if (!change.isFromClient()) return;

		Vehicle vehicle = change.getValue();
		settingsService.updateSelectedVehicle(vehicle);
		fireEvent(new VehicleSelectedEvent(this, vehicle));
	}

	public void refreshVehicleBox() {
		vehicleBox.setItems(mainService.findVehicles());
		settingsService.getSelectedVehicle().ifPresent(vehicleBox::setValue);
	}

	private void createHeader() {
		H1 logo = new H1("Moto Tracker");
		logo.addClassNames(
				LumoUtility.FontSize.LARGE,
				LumoUtility.Margin.MEDIUM
		);

		String u = securityService.getCurrentUsername();
		Button logout = new Button("Log out " + u, click -> securityService.logout()); // <2>

		var header = new HorizontalLayout(new DrawerToggle(), logo, logout);
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
		vehicleBox.setItemLabelGenerator(Vehicle::toStringShort);
		vehicleBox.setWidthFull();
		vehicleBox.addValueChangeListener(this::onVehicleBoxChange);
		refreshVehicleBox();

		// Create Edit Vehicles Button
		var editVehiclesBtn = new Button(new Icon(VaadinIcon.EDIT));
		editVehiclesBtn.addThemeVariants(
				ButtonVariant.LUMO_ICON,
				ButtonVariant.LUMO_TERTIARY,
				ButtonVariant.LUMO_WARNING
		);

		editVehiclesBtn.addClickListener(click -> UI.getCurrent().navigate(VehicleView.class));

		// Combine in Vehicle Layout
		var vehicleLayout = new HorizontalLayout(vehicleBox, editVehiclesBtn);
		vehicleLayout.setWidthFull();
		vehicleLayout.setPadding(false);
		vehicleLayout.setSpacing(false);
		vehicleLayout.setAlignItems(FlexComponent.Alignment.END); // optional

		addToDrawer(new VerticalLayout(
				vehicleLayout,
				new RouterLink("Dashboard", DashboardView.class),
				new RouterLink("Operations", OplistView.class),
				new RouterLink("Services", ServiceView.class),
				new RouterLink("Trackers", TrackerView.class)
		));
	}

	// --- Publish events ---

	public void fireTrackerChangedEvent() {
		fireEvent(new TrackerChangedEvent(this));
	}

	public void fireEventChangedEvent() {
		fireEvent(new EventChangedEvent(this));
	}

	public void fireOperationChangedEvent() {
		fireEvent(new OperationChangedEvent(this));
	}

	// --- Subscribe to events ---

	public void addVehicleSelectedListener(ComponentEventListener<VehicleSelectedEvent> listener) {
		addListener(VehicleSelectedEvent.class, listener);
	}

	public void addTrackerChangedListener(ComponentEventListener<TrackerChangedEvent> listener) {
		addListener(TrackerChangedEvent.class, listener);
	}

	public void addEventChangedListener(ComponentEventListener<EventChangedEvent> listener) {
		addListener(EventChangedEvent.class, listener);
	}

	public void addOperationChangedListener(ComponentEventListener<OperationChangedEvent> listener) {
		addListener(OperationChangedEvent.class, listener);
	}
}