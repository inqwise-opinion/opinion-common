package com.inqwise.opinion.common;

import com.google.inject.AbstractModule;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public abstract class OpinionModule extends AbstractModule {
	protected final Vertx vertx;
	private JsonObject config;

	public OpinionModule(Vertx vertx) {
		this(vertx, vertx.getOrCreateContext().config());
	}
	
	public OpinionModule(Vertx vertx, JsonObject config) {
		this.vertx = vertx;
		this.config = config;
	}
	
	public JsonObject config() {
		return config;
	}
}
