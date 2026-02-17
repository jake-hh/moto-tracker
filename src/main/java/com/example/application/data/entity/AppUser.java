package com.example.application.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


@SuppressWarnings("unused")
@Entity
public class AppUser extends AbstractEntity {

	//private static final java.util.regex.Pattern EMAIL_PATTERN = java.util.regex.Pattern.compile("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$");

	@NotBlank
	@Column(unique = true)
	@Size(min = 4, max = 24, message = "Username must be 4–24 characters")
	@Pattern(regexp = "^[a-z][a-z0-9-]*$", message = "Username must contain lowercase alphanumeric or dash and begin with a lowercase letter")
	private String username;

	@NotBlank
	//@Size(min = 8, max = 24, message = "Password must be 8–24 characters")
	//@Pattern(regexp = "^$|^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).+$", message = "Password must contain at least one lowercase, uppercase and digit")
	private String passwordHash;

	@Size(max = 24, message = "First name exceeds 24 characters")
	@Pattern(regexp = "[\\p{L}]*", message = "First name must contain letters")
	private String firstName;

	@Size(max = 24, message = "Last name exceeds 24 characters")
	@Pattern(regexp = "[\\p{L}]*", message = "Last name must contain letters")
	private String lastName;

	@Size(max = 24, message = "E-mail exceeds 24 characters")
	//@Email(message = "Invalid e-mail address")
	private String email;


	//public static boolean validateEmail(String email) {
	//  return email != null && EMAIL_PATTERN.matcher(email).matches();
	//}

	@Override
	public String toString() {
		return username + " [ " + getId() +  " ]";
	}

	public String getUsername() {
		return username;
	}

	public String getPasswordHash() {
		return passwordHash;
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

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
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
