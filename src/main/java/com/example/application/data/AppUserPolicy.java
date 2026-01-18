package com.example.application.data;

import java.util.regex.Pattern;


public class AppUserPolicy {

	private static final int MIN_USERNAME_LENGTH = 5;
	private static final int MIN_NAME_LENGTH = 2;
	private static final int MIN_PASSWORD_LENGTH = 8;

	private static final Pattern USERNAME_PATTERN =
			Pattern.compile("^[a-z][a-z0-9-]*$");

	private static final Pattern EMAIL_PATTERN =
			Pattern.compile("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$");


	public static boolean isUsernameLongEnough(String s) {
		return s != null && s.length() >= MIN_USERNAME_LENGTH;
	}

	public static boolean isValidUsername(String s) {
		return s != null && USERNAME_PATTERN.matcher(s).matches();
	}

	public static boolean isNameEmptyOrLongEnough(String s) {
		return s == null || s.isEmpty() || s.length() >= MIN_NAME_LENGTH;
	}

	public static boolean isAlphabetic(String s) {
		return s.chars().allMatch(Character::isLetter);
	}

	public static boolean isEmailEmptyOrValid(String s) {
		return s == null || s.isEmpty() || EMAIL_PATTERN.matcher(s).matches();
	}

	public static boolean isPasswordLongEnough(String s) {
		return s != null && s.length() >= MIN_PASSWORD_LENGTH;
	}

	public static boolean hasUppercase(String s) {
		return s.chars().anyMatch(Character::isUpperCase);
	}

	public static boolean hasLowercase(String s) {
		return s.chars().anyMatch(Character::isLowerCase);
	}

	public static boolean hasDigit(String s) {
		return s.chars().anyMatch(Character::isDigit);
	}
}
