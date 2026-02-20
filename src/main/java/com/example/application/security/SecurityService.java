package com.example.application.security;

import com.example.application.ui.Notify;
import com.example.application.data.entity.AppUser;
import com.example.application.data.repo.AppUserRepository;

import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


/* Current user access (Security → Domain) */

@Component
public class SecurityService {

	private final AuthenticationContext authenticationContext;
	private final AppUserRepository userRepository;


	public SecurityService(AuthenticationContext authenticationContext, AppUserRepository userRepository) {
		this.authenticationContext = authenticationContext;
		this.userRepository = userRepository;
	}

	public String getCurrentUsername() {
		return authenticationContext
				.getAuthenticatedUser(UserDetails.class)
				.orElseThrow()
				.getUsername();
	}

	public AppUser getCurrentUser() {
		String username = getCurrentUsername();

		return userRepository.findByUsername(username).orElseThrow();
	}

	public void saveUser(@NotNull AppUser user) {
		if (user == null) {
			Notify.error("user is null. Are you sure you have connected your form to the application?");
			return;
		}

		try {
			userRepository.save(user);
			Notify.ok("Saved user");
		} catch (Exception e) {
			Notify.error("Failed to save user: " + e.getMessage());
			throw new RuntimeException("Could not save user", e);
		}
	}

	public void logout() {
		authenticationContext.logout();
	}
}