package com.inqwise.opinion.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Formatters.
 */
public class Formatters {
	/**
	 * ofPattern.
	 */
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH);
	/**
	 * ofPattern.
	 */
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss", Locale.ENGLISH);

	/**
	 * parseDateTime.
	 */
	public static LocalDateTime parseDateTime(String value) {
		try {
			return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
		} catch (DateTimeParseException ex) {
			return LocalDateTime.parse(value);
		}
	}

	/**
	 * parseDate.
	 */
	public static LocalDate parseDate(String value) {
		try {
			return LocalDate.parse(value, DATE_FORMATTER);
		} catch (DateTimeParseException ex) {
			return LocalDate.parse(value);
		}
	}

	/**
	 * formatDateTime.
	 */
	public static String formatDateTime(LocalDateTime value) {
		return DATE_TIME_FORMATTER.format(value);
	}

	/**
	 * formatDate.
	 */
	public static String formatDate(LocalDate value) {
		return DATE_FORMATTER.format(value);
	}
}
