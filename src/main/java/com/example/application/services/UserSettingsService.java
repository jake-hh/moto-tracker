package com.example.application.services;

import com.example.application.Notify;
import com.example.application.data.*;
import com.example.application.security.SecurityService;

import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@UIScope
public class UserSettingsService {

	private final AppUserSettingsRepository repository;

	private AppUserSettings settings;


	public UserSettingsService(AppUserSettingsRepository repository, SecurityService securityService) {
		this.repository = repository;

		AppUser user = securityService.getCurrentUser();

		repository.findByUser(user)
				.ifPresentOrElse(s -> this.settings = s, () -> save(new AppUserSettings(user)));
	}

	public Optional<Vehicle> getSelectedVehicle() {
		return Optional.ofNullable(settings.getSelectedVehicle());
	}

	public DashboardMode getDashboardMode() {
		return Optional.ofNullable(settings.getDashboardMode())
				.orElse(DashboardMode.LAST_SERVICE);
	}

	public void setSelectedVehicleIfEmpty(@NotNull Vehicle vehicle) {
		if (settings.getSelectedVehicle() == null)
			updateSelectedVehicle(vehicle);
	}

	public void unselectDeletedVehicle(@NotNull Vehicle deleted, @NotNull MainService mainService) {
		Vehicle selected = settings.getSelectedVehicle();

		if (selected == null || deleted.getId().equals(selected.getId())) {
			List<Vehicle> vehicles = mainService.findVehicles();
			vehicles.remove(deleted);
			updateSelectedVehicle(vehicles.isEmpty() ? null : vehicles.getFirst());
		}
	}

	public void updateSelectedVehicle(Vehicle vehicle) {
		settings.setSelectedVehicle(vehicle);
		save(settings);
	}

	public void updateDashboardMode(DashboardMode dashboardMode) {
		settings.setDashboardMode(dashboardMode);
		save(settings);
	}

	private void save(@NotNull AppUserSettings settings) {
		if (settings == null) {
			Notify.error("settings is null. Are you sure you have connected your form to the application?");
			return;
		}

		try {
			this.settings = repository.save(settings);
			// Notify.ok("Saved settings");
		} catch (Exception e) {
			Notify.error("Failed to save settings: " + e.getMessage());
			throw new RuntimeException("Could not save user", e);
		}
	}
}
