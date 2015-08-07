package com.taubler.vxmock;

import io.vertx.core.AbstractVerticle;

public class Embedded extends AbstractVerticle {
	
	public void start() throws Exception {
		new Mock(vertx).start();
	}

}
