package com.inqwise.opinion.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.inqwise.errors.ErrorCodes;
import com.inqwise.errors.ErrorTicket;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.HttpException;

class HttpErrorResponseHandlerTest {

	@Test
	void handle_mapsHttpExceptionToStructuredErrorResponse() {
		var state = TestRoutingContextSupport.contextWithFailure(new HttpException(400, "bad request"));

		new HttpErrorResponseHandler(false, false).handle(state.context());

		JsonObject json = state.response.buffer.toJsonObject();
		assertEquals(400, state.response.statusCode);
		assertEquals("application/json", state.response.headers.get(HttpHeaders.CONTENT_TYPE.toString()));
		assertEquals("ArgumentWrong", json.getString("code"));
		assertEquals(400, json.getInteger("status"));
		assertEquals("bad request", json.getString("detail"));
		assertNull(json.getValue("stack"));
	}

	@Test
	void handle_sanitizesUnexpectedErrorsAndIncludesStackWhenEnabled() {
		RuntimeException ex = new RuntimeException("boom");
		ex.setStackTrace(new StackTraceElement[] { new StackTraceElement("a.b.C", "m", "C.java", 10) });
		var state = TestRoutingContextSupport.contextWithFailure(ex);

		new HttpErrorResponseHandler(true, true).handle(state.context());

		JsonObject json = state.response.buffer.toJsonObject();
		assertEquals(500, state.response.statusCode);
		assertEquals("GeneralError", json.getString("code"));
		assertEquals("Contact support", json.getString("detail"));

		JsonArray stack = json.getJsonArray("stack");
		assertNotNull(stack);
		assertFalse(stack.isEmpty());
	}

	@Test
	void handle_usesProvidedErrorTicketAsIs() {
		ErrorTicket ticket = ErrorTicket.builder()
			.withError(ErrorCodes.NotFound)
			.withStatusCode(404)
			.withDetails("resource missing")
			.build();
		var state = TestRoutingContextSupport.contextWithFailure(ticket);

		new HttpErrorResponseHandler(false, false).handle(state.context());

		JsonObject json = state.response.buffer.toJsonObject();
		assertEquals(404, state.response.statusCode);
		assertEquals("NotFound", json.getString("code"));
		assertEquals("resource missing", json.getString("detail"));
		assertNull(json.getValue("stack"));
	}
}
