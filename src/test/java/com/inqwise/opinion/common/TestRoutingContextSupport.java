package com.inqwise.opinion.common;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

final class TestRoutingContextSupport {
	private TestRoutingContextSupport() {
	}

	static RoutingContextState contextWithFailure(Throwable failure) {
		var state = new RoutingContextState();
		state.failure = failure;
		state.response = new HttpResponseState();
		state.responseProxy = newResponseProxy(state.response);
		state.contextProxy = newContextProxy(state);
		return state;
	}

	private static RoutingContext newContextProxy(RoutingContextState state) {
		return (RoutingContext) Proxy.newProxyInstance(
			RoutingContext.class.getClassLoader(),
			new Class<?>[] { RoutingContext.class },
			(proxy, method, args) -> {
				String name = method.getName();
				if ("failure".equals(name)) {
					return state.failure;
				}
				if ("fail".equals(name) && args != null && args.length >= 1 && args[0] instanceof Throwable) {
					state.failedWith = (Throwable) args[0];
					return null;
				}
				if ("next".equals(name)) {
					state.nextCalls++;
					return null;
				}
				if ("response".equals(name)) {
					return state.responseProxy;
				}
				if ("get".equals(name) && args != null && args.length == 1 && args[0] instanceof String) {
					return state.data.get(args[0]);
				}
				if ("get".equals(name) && args != null && args.length == 2 && args[0] instanceof String) {
					return state.data.containsKey(args[0]) ? state.data.get(args[0]) : args[1];
				}
				if ("put".equals(name) && args != null && args.length == 2 && args[0] instanceof String) {
					state.data.put((String) args[0], args[1]);
					return proxy;
				}
				if ("data".equals(name) && (args == null || args.length == 0)) {
					return state.data;
				}
				return defaultValue(method.getReturnType());
			}
		);
	}

	private static HttpServerResponse newResponseProxy(HttpResponseState state) {
		return (HttpServerResponse) Proxy.newProxyInstance(
			HttpServerResponse.class.getClassLoader(),
			new Class<?>[] { HttpServerResponse.class },
			(proxy, method, args) -> {
				String name = method.getName();
				if ("putHeader".equals(name) && args != null && args.length == 2) {
					state.headers.put(String.valueOf(args[0]), String.valueOf(args[1]));
					return proxy;
				}
				if ("setStatusCode".equals(name) && args != null && args.length == 1 && args[0] instanceof Integer) {
					state.statusCode = (Integer) args[0];
					return proxy;
				}
				if ("getStatusCode".equals(name)) {
					return state.statusCode;
				}
				if ("end".equals(name) && args != null && args.length == 1 && args[0] instanceof Buffer) {
					state.buffer = (Buffer) args[0];
					return null;
				}
				if ("end".equals(name) && (args == null || args.length == 0)) {
					state.buffer = Buffer.buffer();
					return null;
				}
				return defaultValue(method.getReturnType());
			}
		);
	}

	private static Object defaultValue(Class<?> returnType) {
		if (!returnType.isPrimitive()) {
			return null;
		}
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
		return null;
	}

	static final class RoutingContextState {
		final Map<String, Object> data = new HashMap<>();
		RoutingContext contextProxy;
		HttpServerResponse responseProxy;
		Throwable failure;
		Throwable failedWith;
		int nextCalls;
		HttpResponseState response;

		RoutingContext context() {
			return contextProxy;
		}
	}

	static final class HttpResponseState {
		final Map<String, String> headers = new HashMap<>();
		int statusCode;
		Buffer buffer;
	}
}
