package com.example.application.data;


public class BasicPolicy {

	private static final int MIN_STR_LENGTH = 2;
	private static final int MAX_STR_LENGTH = 24;

	public static boolean isAlphabetic(String s) {
		return s.chars().allMatch(Character::isLetter);
	}

	public static boolean isAlphanumeric(String s) {
		return s.chars().allMatch(Character::isLetterOrDigit);
	}

	public static boolean isStrEmptyOrNotTooLong(String s) {
		return s == null || s.length() <= MAX_STR_LENGTH;
	}

	public static boolean isStrEmptyOrLongEnough(String s) {
		return s == null || s.isEmpty() || s.length() >= MIN_STR_LENGTH;
	}
}
