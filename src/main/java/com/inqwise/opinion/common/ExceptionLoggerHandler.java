package com.inqwise.opinion.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.inqwise.errors.ErrorTicket;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.HttpException;

/**
 * ExceptionLoggerHandler.
 */
public class ExceptionLoggerHandler implements Handler<RoutingContext> {
	/**
	 * getLogger.
	 */
	private static final Logger logger = LogManager.getLogger(ExceptionLoggerHandler.class);
	private boolean warnErrorTickets;
	
	/**
	 * Constructs ExceptionLoggerHandler.
	 *
	 * @param warnErrorTickets whether to warn for managed error tickets
	 */
	public ExceptionLoggerHandler(boolean warnErrorTickets) {
		this.warnErrorTickets = warnErrorTickets;
	}
	
	/**
	 * handle.
	 */
	@Override
	public void handle(RoutingContext context) {
		// logger
		Throwable ex = context.failure();
		var isManagedException = (ex instanceof HttpException || ex instanceof ErrorTicket);
		if(!isManagedException) {
			logger.error("Unexpected error occured", ex);
		} else if(warnErrorTickets) {
			logger.warn(ex);
		}
		context.next();
	}
}
