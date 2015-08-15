package com.taubler.vxmock.handlers;

import io.vertx.core.Vertx;

public abstract class AbstractRequestHandler implements RequestHandler {
	
	protected Vertx vertx;

	@Override
	public void setVertx(Vertx vx) {
		this.vertx = vx;
	}

	@Override
	public void postConstruct() {}

}
