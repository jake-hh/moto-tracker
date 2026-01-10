package com.example.application.services;

import com.example.application.Notify;
import com.example.application.data.*;
import com.example.application.security.SecurityService;

import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
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
		// TODO: Instead of checking for default vehicle it when fetching - select it and save in db when saving a new vehicle in VehicleForm
		//return selectDefaultVehicleIfNull(settings.getSelectedVehicle());

		return Optional.ofNullable(settings.getSelectedVehicle());

	}

	public void updateSelectedVehicle(@NotNull Vehicle vehicle) {
		settings.setSelectedVehicle(vehicle);
		save(settings);
	}

	private void save(@NotNull AppUserSettings settings) {
		if (settings == null) {
			Notify.error("settings is null. Are you sure you have connected your form to the application?");
			return;
		}

		try {
			this.settings = repository.save(settings);
		} catch (Exception e) {
			Notify.error("Failed to save settings: " + e.getMessage());
			throw new RuntimeException("Could not save user", e);
		}
	}
}
