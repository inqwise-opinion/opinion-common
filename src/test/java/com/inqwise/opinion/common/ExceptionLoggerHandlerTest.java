package com.inqwise.opinion.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.inqwise.errors.ErrorCodes;
import com.inqwise.errors.ErrorTicket;

import io.vertx.ext.web.handler.HttpException;

class ExceptionLoggerHandlerTest {

	@Test
	void handle_callsNextForUnexpectedException() {
		var state = TestRoutingContextSupport.contextWithFailure(new RuntimeException("boom"));

		new ExceptionLoggerHandler(false).handle(state.context());

		assertEquals(1, state.nextCalls);
		assertNull(state.failedWith);
	}

	@Test
	void handle_callsNextForHttpException() {
		var state = TestRoutingContextSupport.contextWithFailure(new HttpException(400, "bad request"));

		new ExceptionLoggerHandler(false).handle(state.context());

		assertEquals(1, state.nextCalls);
		assertNull(state.failedWith);
	}

	@Test
	void handle_callsNextForManagedErrorTicket() {
		ErrorTicket ticket = ErrorTicket.builder()
			.withError(ErrorCodes.NotFound)
			.withStatusCode(404)
			.withDetails("missing")
			.build();
		var state = TestRoutingContextSupport.contextWithFailure(ticket);

		new ExceptionLoggerHandler(true).handle(state.context());

		assertEquals(1, state.nextCalls);
		assertNull(state.failedWith);
	}
}
