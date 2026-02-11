package com.inqwise.opinion.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.concurrent.CompletionException;

import org.junit.jupiter.api.Test;

import com.inqwise.errors.ErrorCodes;
import com.inqwise.errors.ErrorTicket;

import io.vertx.ext.web.handler.HttpException;

class ExceptionNormalizerHandlerTest {

	@Test
	void handleOnce_callsNextWhenNoFailure() {
		var state = TestRoutingContextSupport.contextWithFailure(null);

		new ExceptionNormalizerHandler().handle(state.context());

		assertEquals(1, state.nextCalls);
		assertNull(state.failedWith);
	}

	@Test
	void handleOnce_callsNextWhenFailureIsManaged() {
		ErrorTicket ticket = ErrorTicket.builder()
			.withError(ErrorCodes.ArgumentWrong)
			.withStatusCode(400)
			.withDetails("bad")
			.build();
		var state = TestRoutingContextSupport.contextWithFailure(ticket);

		new ExceptionNormalizerHandler().handle(state.context());

		assertEquals(1, state.nextCalls);
		assertNull(state.failedWith);
	}

	@Test
	void handleOnce_unboxesCompletionExceptionAndFailsWithCause() {
		Throwable cause = new IllegalStateException("db down");
		var state = TestRoutingContextSupport.contextWithFailure(new CompletionException(cause));

		new ExceptionNormalizerHandler().handle(state.context());

		assertEquals(1, state.nextCalls);
		assertSame(cause, state.failedWith);
	}

	@Test
	void handleOnce_ignoresHttpExceptionAsManaged() {
		var state = TestRoutingContextSupport.contextWithFailure(new HttpException(404, "missing"));

		new ExceptionNormalizerHandler().handle(state.context());

		assertEquals(1, state.nextCalls);
		assertNull(state.failedWith);
	}
}
