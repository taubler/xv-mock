package com.taubler.vxmock.routes;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.impl.RouterImpl;


public class DelegatingRouteMatcher extends RouterImpl  {
	
	public DelegatingRouteMatcher(Vertx vertx) {
		super(vertx);
	}

	private VxMockRouteMatcher delegateRouteMatcher;
	
	public void setDelegateRouteMatcher(VxMockRouteMatcher matcher) {
		this.delegateRouteMatcher = matcher;
	}
	
	public VxMockRouteMatcher getDelegateRouteMatcher() {
		return delegateRouteMatcher;
	}
	
	@Override
	public void accept(HttpServerRequest request) {
		delegateRouteMatcher.accept(request);
	}

}
