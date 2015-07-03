package com.taubler.vxmock;

import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.http.HttpServer;

import com.taubler.vxmock.io.CommandListener;
import com.taubler.vxmock.io.RuntimeMessager;
import com.taubler.vxmock.routes.DelegatingRouteMatcher;
import com.taubler.vxmock.routes.RouteCreator;
import com.taubler.vxmock.routes.VxMockRouteMatcher;

public class Mock {
	
	private static final int MIN_PORT = 1;
	private static final int MAX_PORT = 65535;
	
	private DelegatingRouteMatcher delegatingMatcher = new DelegatingRouteMatcher();
	
	protected void start(String[] args) throws Exception {
		RuntimeMessager.output("Starting vx-mock...");

		int port = 8099;
		if (args.length > 0) {
			String portStr = args[0];
			try {
				int suggPort = Integer.parseInt(portStr);
				if (suggPort >= MIN_PORT && suggPort <= MAX_PORT) {
					port = suggPort;
				} else {
					RuntimeMessager.output( 
							String.format("Could not use '%d' as a port (not between %s and %d); defaulting to %d\n", 
							suggPort, MIN_PORT, MAX_PORT, port) );
				}
			} catch (Exception e) {
				RuntimeMessager.output( 
						String.format("Could not use '%s' as a port; defaulting to %d\n", portStr, port) );
			}
			
		}
		Vertx vertx = VertxFactory.newVertx();
		loadRoutes();
		HttpServer httpServer = vertx.createHttpServer().requestHandler(delegatingMatcher).listen(port);
		
		RuntimeMessager.output("Webserver started, listening on port: " + port);
        
        new CommandListener().listen(this);
	}

	public void loadRoutes() throws Exception {
		RouteCreator routeCreator = new RouteCreator();
		VxMockRouteMatcher matcher = routeCreator.createRoutes();
		delegatingMatcher.setDelegateRouteMatcher(matcher);
	}
	
	public VxMockRouteMatcher getRouteMatcher() {
		return delegatingMatcher.getDelegateRouteMatcher();
	}

}
