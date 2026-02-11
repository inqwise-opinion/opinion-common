package com.inqwise.opinion.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.junit5.VertxExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Formatters}.
 */
@ExtendWith(VertxExtension.class)
class FormattersTest {

	@Test
	void parseDateTime_withCustomPattern_parsesSuccessfully() {
		LocalDateTime parsed = Formatters.parseDateTime("Jan 05, 2026 13:45:59");

		assertEquals(LocalDateTime.of(2026, 1, 5, 13, 45, 59), parsed);
	}

	@Test
	void parseDateTime_withIsoFormat_usesFallbackParser() {
		LocalDateTime parsed = Formatters.parseDateTime("2026-02-11T10:15:30");

		assertEquals(LocalDateTime.of(2026, 2, 11, 10, 15, 30), parsed);
	}

	@Test
	void parseDateTime_withInvalidValue_throwsException() {
		assertThrows(DateTimeParseException.class, () -> Formatters.parseDateTime("not-a-date-time"));
	}

	@Test
	void parseDate_withCustomPattern_parsesSuccessfully() {
		LocalDate parsed = Formatters.parseDate("Feb 11, 2026");

		assertEquals(LocalDate.of(2026, 2, 11), parsed);
	}

	@Test
	void parseDate_withIsoFormat_usesFallbackParser() {
		LocalDate parsed = Formatters.parseDate("2026-02-11");

		assertEquals(LocalDate.of(2026, 2, 11), parsed);
	}

	@Test
	void parseDate_withInvalidValue_throwsException() {
		assertThrows(DateTimeParseException.class, () -> Formatters.parseDate("not-a-date"));
	}

	@Test
	void formatDateTime_withValidValue_returnsExpectedPattern() {
		String formatted = Formatters.formatDateTime(LocalDateTime.of(2026, 3, 7, 8, 9, 10));

		assertEquals("Mar 07, 2026 08:09:10", formatted);
	}

	@Test
	void formatDate_withValidValue_returnsExpectedPattern() {
		String formatted = Formatters.formatDate(LocalDate.of(2026, 3, 7));

		assertEquals("Mar 07, 2026", formatted);
	}
}
