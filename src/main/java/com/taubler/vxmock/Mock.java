package com.taubler.vxmock;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;

import com.taubler.vxmock.io.CommandListener;
import com.taubler.vxmock.io.RuntimeMessager;
import com.taubler.vxmock.routes.DelegatingRouteMatcher;
import com.taubler.vxmock.routes.RouteCreator;
import com.taubler.vxmock.routes.VxMockRouteMatcher;

public class Mock {
	
	private DelegatingRouteMatcher delegatingMatcher;
	
	private Vertx vertx ;
	
	public Mock(Vertx vx) {
		this.vertx = vx;
	}
	
	protected void start() throws Exception {
		delegatingMatcher = new DelegatingRouteMatcher(vertx);
		RuntimeMessager.output("Starting vx-mock...");

		LaunchContext ctx = LaunchContextFactory.findLaunchContext();
		loadRoutes();
		HttpServer server = vertx.createHttpServer().requestHandler(delegatingMatcher::accept).listen(ctx.port);
		
		RuntimeMessager.output("Webserver started, listening on port: " + ctx.port);
        
        new CommandListener(this).listen();
	}

	public void loadRoutes() throws Exception {
		RouteCreator routeCreator = new RouteCreator();
		VxMockRouteMatcher matcher = routeCreator.createRoutes(vertx);
		delegatingMatcher.setDelegateRouteMatcher(matcher);
	}
	
	public VxMockRouteMatcher getRouteMatcher() {
		return delegatingMatcher.getDelegateRouteMatcher();
	}

}
