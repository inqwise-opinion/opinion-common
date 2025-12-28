package com.inqwise.opinion.common;

import com.google.inject.AbstractModule;

import io.vertx.core.Vertx;

public abstract class OpinionModule extends AbstractModule {
	protected final Vertx vertx;

	public OpinionModule(Vertx vertx) {
		this.vertx = vertx;
	}
}
