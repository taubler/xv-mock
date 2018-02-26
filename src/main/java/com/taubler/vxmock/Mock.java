package com.taubler.vxmock;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.taubler.vxmock.io.CommandListener;
import com.taubler.vxmock.io.RuntimeMessager;
import com.taubler.vxmock.routes.DelegatingRouteMatcher;
import com.taubler.vxmock.routes.RouteCreator;
import com.taubler.vxmock.routes.VxMockRouteMatcher;

public class Mock {
	
//	private DelegatingRouteMatcher delegatingMatcher;
	private final HashMap<Integer, DelegatingRouteMatcher> delegatingMatchers = new HashMap<>();
	
	private Vertx vertx ;
	
	public Mock(Vertx vx) {
		this.vertx = vx;
	}
	
	protected void start() throws Exception {
		RuntimeMessager.output("Starting vx-mock...");

		LaunchContext ctx = LaunchContextFactory.findLaunchContext();
		loadRoutes();
		
		delegatingMatchers.forEach((k,v) -> {
            HttpServer server = vertx.createHttpServer().requestHandler(v::accept).listen(k);
            RuntimeMessager.output("Webserver started, listening on port: " + k);		    
		});
		
		CommandListener cl = new CommandListener(this);
		vertx.deployVerticle(cl);
	}

	public void loadRoutes() throws Exception {
		RouteCreator routeCreator = new RouteCreator();
		Map<Integer, VxMockRouteMatcher> matchers = routeCreator.createRoutes(vertx);
		matchers.forEach((k, v) -> {
		    DelegatingRouteMatcher delegatingMatcher = new DelegatingRouteMatcher(vertx);
		    delegatingMatcher.setDelegateRouteMatcher(v);
		    delegatingMatchers.put(k, delegatingMatcher);
		});
	}
	
	public Map<Integer, VxMockRouteMatcher> getRouteMatchers() {
	    return delegatingMatchers.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getDelegateRouteMatcher()));
	}
	
	public void printRouteMatchers() {
        getRouteMatchers().forEach((k, v) -> {
            RuntimeMessager.output( String.format("[Port %d]", k) );
            v.print();
            RuntimeMessager.output( String.format("[/Port %d]", k) );
        });
	}

}
