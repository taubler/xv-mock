package com.taubler.vxmock.routes;

import java.util.HashMap;
import java.util.Map;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;

import com.taubler.vxmock.io.RuntimeMessager;

public class VxMockRouteMatcher extends RouteMatcher {
	
	private Map<String, String> routeDescriptions = new HashMap<>();
	
	public void addRoute(String path, String filePath) {
		routeDescriptions.put(path, filePath);
		get( path, handlerFor(filePath) );
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
