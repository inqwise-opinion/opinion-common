package com.inqwise.opinion.common;

import java.util.concurrent.CompletionException;

import com.inqwise.errors.ErrorTicket;
import com.inqwise.errors.StackTraceFocuser;
import com.inqwise.errors.Throws;

import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.HttpException;

/**
 * ExceptionNormalizerHandler.
 */
public class ExceptionNormalizerHandler extends OncePerRoutingContextHandler {
	
	private final StackTraceFocuser focuser;
	/**
	 * Constructs ExceptionNormalizerHandler.
	 */
	public ExceptionNormalizerHandler() {
		focuser = StackTraceFocuser.builder()
				.addClass("^io\\.vertx\\.sqlclient")
				.addClass("^io\\.vertx\\.mysqlclient")
				.addClass("^com\\.google\\.common")
				.build();
	}
	
	/**
	 * handleOnce.
	 */
	@Override
	public void handleOnce(RoutingContext context) {
		var ex = context.failure();
		if(null == ex || ex instanceof HttpException || ex instanceof ErrorTicket) {
			context.next();
		}
		else {
			ex = Throws.unbox(ex, CompletionException.class);
			ex = focuser.apply(ex);
			
			context.fail(ex);
			context.next();
		}
	}
}
