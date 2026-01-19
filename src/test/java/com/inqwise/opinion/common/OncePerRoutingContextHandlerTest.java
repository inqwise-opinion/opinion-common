package com.inqwise.opinion.common;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import io.vertx.ext.web.RoutingContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link OncePerRoutingContextHandler}.
 */
class OncePerRoutingContextHandlerTest {

	@Test
	void handle_invokesHandleOnceOnlyOncePerContext() {
		AtomicInteger calls = new AtomicInteger();
		OncePerRoutingContextHandler handler = new OncePerRoutingContextHandler() {
			@Override
			public void handleOnce(RoutingContext context) {
				calls.incrementAndGet();
			}
		};

		RoutingContext context = newTestContext();

		handler.handle(context);
		handler.handle(context);

		assertEquals(1, calls.get());
	}

	@Test
	void handle_invokesHandleOnceForDifferentContexts() {
		AtomicInteger calls = new AtomicInteger();
		OncePerRoutingContextHandler handler = new OncePerRoutingContextHandler() {
			@Override
			public void handleOnce(RoutingContext context) {
				calls.incrementAndGet();
			}
		};

		handler.handle(newTestContext());
		handler.handle(newTestContext());

		assertEquals(2, calls.get());
	}

	@Test
	void handle_setsSeenKeyInContext() {
		OncePerRoutingContextHandler handler = new OncePerRoutingContextHandler() {
			@Override
			public void handleOnce(RoutingContext context) {
				// no-op
			}
		};

		RoutingContext context = newTestContext();
		handler.handle(context);

		String seenKey = "SEEN-" + handler.getClass().getSimpleName();
		assertEquals(Boolean.TRUE, context.get(seenKey));
	}

	private static RoutingContext newTestContext() {
		Map<String, Object> data = new HashMap<>();
		return (RoutingContext) Proxy.newProxyInstance(
			RoutingContext.class.getClassLoader(),
			new Class<?>[] { RoutingContext.class },
			(proxy, method, args) -> {
				String name = method.getName();
				if ("get".equals(name) && args != null && args.length == 1 && args[0] instanceof String) {
					return data.get(args[0]);
				}
				if ("put".equals(name) && args != null && args.length == 2 && args[0] instanceof String) {
					data.put((String) args[0], args[1]);
					return proxy;
				}
				if ("data".equals(name) && (args == null || args.length == 0)) {
					return data;
				}
				Class<?> returnType = method.getReturnType();
				if (returnType.isPrimitive()) {
					if (returnType == boolean.class) {
						return false;
					}
					if (returnType == byte.class) {
						return (byte) 0;
					}
					if (returnType == short.class) {
						return (short) 0;
					}
					if (returnType == int.class) {
						return 0;
					}
					if (returnType == long.class) {
						return 0L;
					}
					if (returnType == float.class) {
						return 0f;
					}
					if (returnType == double.class) {
						return 0d;
					}
					if (returnType == char.class) {
						return '\0';
					}
				}
				return null;
			}
		);
	}
}
