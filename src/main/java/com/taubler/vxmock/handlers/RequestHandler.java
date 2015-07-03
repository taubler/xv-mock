package com.taubler.vxmock.handlers;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;

public interface RequestHandler extends Handler<HttpServerRequest> {
	
	static final int PRECENDENCE_FIRST = 0;
	static final int PRECENDENCE_LAST = 99;
	static final int PRECENDENCE_MIDDLE = 49;
	
	int precedence();

}
