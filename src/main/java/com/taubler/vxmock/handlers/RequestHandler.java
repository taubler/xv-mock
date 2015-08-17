package com.taubler.vxmock.handlers;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;


public interface RequestHandler extends Handler<RoutingContext> {
	
	static final int PRECENDENCE_FIRST = 0;
	static final int PRECENDENCE_LAST = 99;
	static final int PRECENDENCE_MIDDLE = 49;
	
	int precedence();
	
	void setVertx(Vertx vx);
	
	void postConstruct();

}
