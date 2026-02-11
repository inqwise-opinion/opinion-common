package com.inqwise.opinion.common;

import java.util.Objects;

import com.inqwise.errors.ErrorCodes;
import com.inqwise.errors.ErrorTicket;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.HttpException;

/**
 * HttpErrorResponseHandler.
 */
public class HttpErrorResponseHandler implements Handler<RoutingContext> {
		
	private boolean printStackTrace;
	private boolean sanitizeUnexpectedErrors;

	/**
	 * Constructs HttpErrorResponseHandler.
	 *
	 * @param printStackTrace whether to include stack traces
	 * @param sanitizeUnexpectedErrors whether to sanitize unexpected errors
	 */
	public HttpErrorResponseHandler(boolean printStackTrace, boolean sanitizeUnexpectedErrors) {
		this.printStackTrace = printStackTrace;
		this.sanitizeUnexpectedErrors = sanitizeUnexpectedErrors;
	}
	
	/**
	 * handle.
	 */
	@Override
	public void handle(RoutingContext ctx) {
		ErrorTicket et;
		Throwable ex = ctx.failure();
		if(ex instanceof HttpException) {
			et = parse((HttpException)ex);
		} else if(ex instanceof ErrorTicket) {
			et = (ErrorTicket)ex;
		} else {
			et = ErrorTicket.propagate(ex, b -> {
				if(b.build().getError() == ErrorCodes.GeneralError && sanitizeUnexpectedErrors) {
					b.withErrorDetails("Contact support");
				}
			});
		}
		
		var json = et.toJson();
		if(printStackTrace && null != ex.getStackTrace() && 0 != ex.getStackTrace().length) {
			json.put("stack", stackTraceToJson(ex.getStackTrace()));
		}
		
		ctx.response()
		.putHeader(HttpHeaders.CONTENT_TYPE, et.getContentType())
		.setStatusCode(Objects.requireNonNullElse(et.getStatus(), 500))
		.end(json.toBuffer());
	}

	/**
	 * parse.
	 */
	private static ErrorTicket parse(HttpException ex) {
		var builder = ErrorTicket.builder()
				.withStatusCode(ex.getStatusCode())
				.withDetails(ex.getPayload());
		
		switch(ex.getStatusCode()) {
		case 400:
			builder.withError(ErrorCodes.ArgumentWrong);
		break;
		case 404:
			builder.withError(ErrorCodes.NotFound);
		break;
		default:
			builder.withError(ErrorCodes.GeneralError);
		}
		
		return builder.build();
	}
	
	/**
	 * stackTraceToJson.
	 *
	 * @param frames stack trace frames
	 * @return stack trace as json array
	 */
	public static JsonArray stackTraceToJson(StackTraceElement[] frames) {
		JsonArray arr = new JsonArray();
		if (frames == null) return arr;

		for (StackTraceElement f : frames) {
			arr.add(new JsonObject()
					.put("at", f.getClassName() + "." + f.getMethodName())
					.put("src", source(f)));
		}
		return arr;
	}

	/**
	 * source.
	 */
	private static String source(StackTraceElement f) {
		String file = f.getFileName();
		int line = f.getLineNumber();

		if (file == null) return "?";
		if (line < 0) return file;

		return file + ":" + line;
	}
}
