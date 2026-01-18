package com.example.application.services;

import com.example.application.data.entity.AppUser;
import com.example.application.data.repo.AppUserRepository;

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
	public void register(String username, String rawPassword) {

		if (userRepository.existsByUsername(username)) {
			throw new IllegalStateException("Username already exists");
		}

		AppUser user = new AppUser();
		user.setUsername(username);
		user.setPasswordHash(passwordEncoder.encode(rawPassword));

		userRepository.save(user);
	}
}
