package com.inqwise.opinion.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RestApiServerOptionsTest {

	@Test
	void toJson_serializesAllConfiguredFields() {
		RestApiServerOptions options = RestApiServerOptions.builder()
				.withHttpPort(9090)
				.withHttpHost("0.0.0.0")
				.withLogErrorTickets(true)
				.withPrintStacktrace(true)
				.withSanitizeUnexpectedErrors(true)
				.build();

		var json = options.toJson();

		assertEquals(9090, json.getInteger(RestApiServerOptions.Keys.HTTP_PORT));
		assertEquals("0.0.0.0", json.getString(RestApiServerOptions.Keys.HTTP_HOST));
		assertEquals(true, json.getBoolean(RestApiServerOptions.Keys.LOG_ERROR_TICKETS));
		assertEquals(true, json.getBoolean(RestApiServerOptions.Keys.PRINT_STACKTRACE));
		assertEquals(true, json.getBoolean(RestApiServerOptions.Keys.SANITIZE_UNEXPECTED_ERRORS));
	}
}
