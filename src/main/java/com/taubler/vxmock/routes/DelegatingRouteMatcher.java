package com.taubler.vxmock.routes;

import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;

public class DelegatingRouteMatcher extends RouteMatcher {
	
	private VxMockRouteMatcher delegateRouteMatcher;
	
	public void setDelegateRouteMatcher(VxMockRouteMatcher matcher) {
		this.delegateRouteMatcher = matcher;
	}
	
	public VxMockRouteMatcher getDelegateRouteMatcher() {
		return delegateRouteMatcher;
	}
	
	@Override
	public void handle(HttpServerRequest request) {
		delegateRouteMatcher.handle(request);
	}

}
