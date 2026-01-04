package com.example.application.security;

import com.example.application.data.AppUser;
import com.example.application.data.AppUserRepository;
import com.vaadin.flow.spring.security.AuthenticationContext;
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

	public void logout() {
		authenticationContext.logout();
	}
}