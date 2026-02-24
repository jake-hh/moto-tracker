package com.example.application.ui.dto;

import jakarta.validation.constraints.*;


/* Registration Data Transfer Object */

public class RegistrationDTO {

	@NotBlank
	@Size(min = 4, max = 24, message = "Username must be 4–24 characters")
	@Pattern(regexp = "^[a-z][a-z0-9-]*$", message = "Username must contain lowercase alphanumeric or dash and begin with a lowercase letter")
	private String username;

	@NotBlank
	@Size(min = 8, max = 24, message = "Password must be 8–24 characters") // bcrypt safe upper bound: 72
	@Pattern(regexp = "^$|^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).+$", message = "Password must contain at least one lowercase, uppercase and digit")
	private String password;

	@NotBlank
	private String passwordConfirm;

	@Size(max = 24, message = "First name exceeds 24 characters")
	@Pattern(regexp = "[\\p{L}]*", message = "First name must contain letters")
	private String firstName;

	@Size(max = 24, message = "Last name exceeds 24 characters")
	@Pattern(regexp = "[\\p{L}]*", message = "Last name must contain letters")
	private String lastName;

	@Email(message = "Invalid e-mail address")
	@Size(max = 24, message = "E-mail exceeds 24 characters")
	private String email;


	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}