package com.example.application.services;

import com.example.application.data.entity.AppUser;
import com.example.application.data.repo.AppUserRepository;

import com.example.application.ui.Notify;
import com.example.application.ui.dto.RegistrationDTO;
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
	public void register(RegistrationDTO form) {
		// TODO: Add service validation for all fields

		if (!isUsernameAvailable(form.getUsername()))
			throw new IllegalStateException("Username already exists");

		AppUser user = new AppUser();
		user.setUsername(form.getUsername());
		user.setFirstName(form.getFirstName());
		user.setLastName(form.getLastName());
		user.setEmail(form.getEmail());

		user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
		//user.setRole("USER");

		userRepository.save(user);
		Notify.ok("Account created");
	}

	public boolean isUsernameAvailable(String username) {
		return !userRepository.existsByUsername(username);
	}
}
