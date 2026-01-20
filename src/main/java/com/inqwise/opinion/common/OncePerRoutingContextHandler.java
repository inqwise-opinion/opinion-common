package com.inqwise.opinion.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public abstract class OncePerRoutingContextHandler implements Handler<RoutingContext> {
	private static final Logger logger = LogManager.getLogger(OncePerRoutingContextHandler.class);
	
	private final String seenKey; 
	public OncePerRoutingContextHandler() {
		seenKey = "SEEN-" + this.getClass().getSimpleName();
	}
	
	@Override
	public void handle(RoutingContext context) {
		if(null == context.get(seenKey)) {
			context.put(seenKey, true);
			if(logger.isTraceEnabled()) {
				logger.trace(seenKey);
			}
			handleOnce(context);
		}
		
	}
	
	public abstract void handleOnce(RoutingContext context); 
}
