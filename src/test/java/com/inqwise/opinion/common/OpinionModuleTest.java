package com.inqwise.opinion.common;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Guice;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;

@ExtendWith(VertxExtension.class)
class OpinionModuleTest {
	
private static final Logger logger = LogManager.getLogger(OpinionModuleTest.class);
	
	@BeforeEach
	void setUp(Vertx vertx) throws Exception {
	}
	
	@Test
	void testOpinionModule(Vertx vertx) {
		logger.debug("testOpinionModule - start");
		Guice.createInjector(List.of(new OpinionModule(vertx) {
		})).injectMembers(this);
	}

}
