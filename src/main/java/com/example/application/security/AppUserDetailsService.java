package com.example.application.security;

import com.example.application.data.repo.AppUserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/* Authentication provider (DB → Security) */

@Service
public class AppUserDetailsService implements UserDetailsService {

	private final AppUserRepository userRepository;


	public AppUserDetailsService(AppUserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) {
		return userRepository.findByUsername(username)
				.map(AppUserDetails::new)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
	}
}
