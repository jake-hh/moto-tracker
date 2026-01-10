package com.example.application.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AppUserSettingsRepository
		extends JpaRepository<AppUserSettings, Long> {

	Optional<AppUserSettings> findByUser(AppUser user);
}
