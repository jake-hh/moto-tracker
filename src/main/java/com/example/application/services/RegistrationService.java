package com.example.application.services;

import com.example.application.data.entity.AppUser;
import com.example.application.data.repo.AppUserRepository;

import com.example.application.ui.Notify;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class RegistrationService {

	private final AppUserRepository userRepository;
	private final PasswordEncoder passwordEncoder;


	public RegistrationService(
			AppUserRepository userRepository,
			PasswordEncoder passwordEncoder
	) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public void register(AppUser user) {
		// TODO: Add service validation for all fields

		if (!isUsernameAvailable(user.getUsername()))
			throw new IllegalStateException("Username already exists");

		user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
		//user.setRole("USER");

		userRepository.save(user);
		Notify.ok("Account created");
	}

	public boolean isUsernameAvailable(String username) {
		return !userRepository.existsByUsername(username);
	}
}
