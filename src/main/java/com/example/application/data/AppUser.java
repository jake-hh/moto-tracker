package com.example.application.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;

@Entity
public class AppUser extends AbstractEntity {

	@NotBlank
	@Column(nullable = false, unique = true)
	private String username;

	@NotBlank
	@Column(nullable = false)
	private String passwordHash;

	public String getUsername() {
		return username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
}
