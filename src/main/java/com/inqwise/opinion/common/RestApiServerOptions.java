package com.inqwise.opinion.common;

import io.vertx.core.json.JsonObject;
import com.google.common.base.MoreObjects;

public class RestApiServerOptions {
	private Integer httpPort;
	private String httpHost;
	private Boolean logErrorTickets;
	private Boolean printStacktrace;
	private Boolean sanitizeUnexpectedErrors;

	private RestApiServerOptions(Builder builder) {
		this.httpPort = builder.httpPort;
		this.httpHost = builder.httpHost;
		this.logErrorTickets = builder.logErrorTickets;
		this.printStacktrace = builder.printStacktrace;
		this.sanitizeUnexpectedErrors = builder.sanitizeUnexpectedErrors;
	}
	public static class Keys {
		public static final String HTTP_PORT = "http-port";
		public static final String HTTP_HOST = "http-host";
		public static final String LOG_ERROR_TICKETS = "log_error_tickets";
		public static final String PRINT_STACKTRACE = "print_stacktrace";
		public static final String SANITIZE_UNEXPECTED_ERRORS = "sanitize_unexpected_errors";
	}
	
	public RestApiServerOptions(JsonObject json) {
		httpPort = json.getInteger(Keys.HTTP_PORT);
		httpHost = json.getString(Keys.HTTP_HOST);
		logErrorTickets = json.getBoolean(Keys.LOG_ERROR_TICKETS);
		printStacktrace = json.getBoolean(Keys.PRINT_STACKTRACE);
		sanitizeUnexpectedErrors = json.getBoolean(Keys.SANITIZE_UNEXPECTED_ERRORS);
	}

	public static final RestApiServerOptions DEFAULT = RestApiServerOptions.builder().withHttpHost("127.0.0.1")
			.withHttpPort(8080)
			.withLogErrorTickets(false)
			.withPrintStacktrace(false)
			.withSanitizeUnexpectedErrors(false)
			.build();

	public String getHttpHost() {
		return httpHost;
	}
	
	public Integer getHttpPort() {
		return httpPort;
	}
	
	public Boolean getPrintStacktrace() {
		return printStacktrace;
	}
	
	public Boolean getLogErrorTickets() {
		return logErrorTickets;
	}
	
	public Boolean getSanitizeUnexpectedErrors() {
		return sanitizeUnexpectedErrors;
	}
	
	public JsonObject toJson() {
		var json = new JsonObject();

		return json;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("httpPort", httpPort).add("httpHost", httpHost)
				.add("logErrorTickets", logErrorTickets).add("printStacktrace", printStacktrace)
				.add("sanitizeUnexpectedErrors", sanitizeUnexpectedErrors).toString();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static Builder builderFrom(RestApiServerOptions restApiServerOptions) {
		return new Builder(restApiServerOptions);
	}

	public static final class Builder {
		private Integer httpPort;
		private String httpHost;
		private Boolean logErrorTickets;
		private Boolean printStacktrace;
		private Boolean sanitizeUnexpectedErrors;

		private Builder() {
		}

		private Builder(RestApiServerOptions restApiServerOptions) {
			this.httpPort = restApiServerOptions.httpPort;
			this.httpHost = restApiServerOptions.httpHost;
			this.logErrorTickets = restApiServerOptions.logErrorTickets;
			this.printStacktrace = restApiServerOptions.printStacktrace;
			this.sanitizeUnexpectedErrors = restApiServerOptions.sanitizeUnexpectedErrors;
		}

		public Builder withHttpPort(Integer httpPort) {
			this.httpPort = httpPort;
			return this;
		}

		public Builder withHttpHost(String httpHost) {
			this.httpHost = httpHost;
			return this;
		}

		public Builder withLogErrorTickets(Boolean logErrorTickets) {
			this.logErrorTickets = logErrorTickets;
			return this;
		}

		public Builder withPrintStacktrace(Boolean printStacktrace) {
			this.printStacktrace = printStacktrace;
			return this;
		}

		public Builder withSanitizeUnexpectedErrors(Boolean sanitizeUnexpectedErrors) {
			this.sanitizeUnexpectedErrors = sanitizeUnexpectedErrors;
			return this;
		}

		public RestApiServerOptions build() {
			return new RestApiServerOptions(this);
		}
	}
}
