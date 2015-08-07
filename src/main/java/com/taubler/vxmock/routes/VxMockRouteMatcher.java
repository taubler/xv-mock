package com.taubler.vxmock.routes;


import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.impl.RouterImpl;

import java.util.HashMap;
import java.util.Map;

import com.taubler.vxmock.handlers.RequestHandlerDelegate;
import com.taubler.vxmock.io.RuntimeMessager;

public class VxMockRouteMatcher extends RouterImpl {
	
	private Map<String, String> routeDescriptions = new HashMap<>();
	
	public VxMockRouteMatcher(Vertx vertx) {
		super(vertx);
	}
	
	public void addRoute(String path, RequestHandlerDelegate handler, RequestPath.Method method) {
		routeDescriptions.put(String.format("[%s] %s", method.name(), path), handler.toString() );
		switch (method) {
		case _ANY:
			route( path ).handler(rctx -> {handler.handle(rctx.request());});
			break;
		case GET:
			get(path).handler(rctx -> {handler.handle(rctx.request());});
			break;
		case POST:
			post(path).handler(rctx -> {handler.handle(rctx.request());});
			break;
		case PUT:
			put(path).handler(rctx -> {handler.handle(rctx.request());});
			break;
		case PATCH:
			patch(path).handler(rctx -> {handler.handle(rctx.request());});
			break;
		case DELETE:
			delete(path).handler(rctx -> {handler.handle(rctx.request());});
			break;
		case HEAD:
			head(path).handler(rctx -> {handler.handle(rctx.request());});
			break;
		case OPTIONS:
			options(path).handler(rctx -> {handler.handle(rctx.request());});
			break;
		case CONNECT:
			connect(path).handler(rctx -> {handler.handle(rctx.request());});
			break;
		case TRACE:
			trace(path).handler(rctx -> {handler.handle(rctx.request());});
			break;
		}
	}
	
	public static Handler<HttpServerRequest> handlerFor(final String filePath) {
		return new Handler<HttpServerRequest>() {
            public void handle(HttpServerRequest req) {
                System.out.println("Request for file: " + filePath + " based on path " + req.path());
                req.response().sendFile(filePath);
            }
        };
	}
	
	public void print() {
		for (String path : routeDescriptions.keySet()) {
			RuntimeMessager.output( String.format("%s  =>  %s", path, routeDescriptions.get(path)) );
		}
	}

}
