package com.taubler.vxmock.routes;


import java.util.HashMap;
import java.util.Map;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;

import com.taubler.vxmock.handlers.RequestHandlerDelegate;
import com.taubler.vxmock.io.RuntimeMessager;

public class VxMockRouteMatcher extends RouteMatcher {
	
	private Map<String, String> routeDescriptions = new HashMap<>();
	
	public void addRoute(String path, RequestHandlerDelegate handler, RequestPath.Method method) {
		routeDescriptions.put(String.format("[%s] %s", method.name(), path), handler.toString() );
		switch (method) {
		case _ANY:
			all( path, handler );
			break;
		case GET:
			get( path, handler );
			break;
		case POST:
			post( path, handler );
			break;
		case PUT:
			put( path, handler );
			break;
		case PATCH:
			patch( path, handler );
			break;
		case DELETE:
			delete( path, handler );
			break;
		case HEAD:
			head( path, handler );
			break;
		case OPTIONS:
			options( path, handler );
			break;
		case CONNECT:
			connect( path, handler );
			break;
		case TRACE:
			trace( path, handler );
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
