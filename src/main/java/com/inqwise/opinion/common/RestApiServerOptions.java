package com.inqwise.opinion.common;

import io.vertx.core.json.JsonObject;

public class RestApiServerOptions {
	private Integer httpPort;
	private String httpHost;
	private Boolean logErrorTickets;
	private Boolean includeStacktrace;
	private Boolean printOriginalUnexpectedErrors;
	public static class Keys {
		public static final String HTTP_PORT = "http-port";
		public static final String HTTP_HOST = "http-host";
		public static final String LOG_ERROR_TICKETS = "log_error_tickets";
		public static final String INCLUDE_STACKTRACE = "include_stacktrace";
		public static final String PRINT_ORIGINAL_UNEXPECTED_ERRORS = "print_original_unexpected_errors";
	}

	public RestApiServerOptions(JsonObject json) {
		httpPort = json.getInteger(Keys.HTTP_PORT);
		httpHost = json.getString(Keys.HTTP_HOST);
		logErrorTickets = json.getBoolean(Keys.LOG_ERROR_TICKETS);
		includeStacktrace = json.getBoolean(Keys.INCLUDE_STACKTRACE);
		printOriginalUnexpectedErrors = json.getBoolean(Keys.PRINT_ORIGINAL_UNEXPECTED_ERRORS);
	}

	public String getHttpHost() {
		return httpHost;
	}
	
	public Integer getHttpPort() {
		return httpPort;
	}
	
	public Boolean getIncludeStacktrace() {
		return includeStacktrace;
	}
	
	public Boolean getLogErrorTickets() {
		return logErrorTickets;
	}
	
	public Boolean getPrintOriginalUnexpectedErrors() {
		return printOriginalUnexpectedErrors;
	}
	
	public JsonObject toJson() {
		var json = new JsonObject();

		return json;
	}
}
