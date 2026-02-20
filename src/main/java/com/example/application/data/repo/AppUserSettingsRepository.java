package com.example.application.data.repo;

import com.example.application.data.entity.AppUser;
import com.example.application.data.entity.AppUserSettings;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AppUserSettingsRepository extends JpaRepository<AppUserSettings, Long> {

	Optional<AppUserSettings> findByUser(AppUser user);
}
