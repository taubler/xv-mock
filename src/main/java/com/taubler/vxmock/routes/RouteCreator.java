package com.taubler.vxmock.routes;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.taubler.vxmock.handlers.RequestHandlerDelegate;
import com.taubler.vxmock.routes.RequestPath.Method;

public class RouteCreator {
	
	private static final List<? extends RouteFileParser> routeFileParsers = 
			Arrays.asList(new JsonRouteFileParser()
			);
	
	public Map<Integer, VxMockRouteMatcher> createRoutes(Vertx vx) throws Exception {
		
//		Map<RequestPath, List<RequestHandler>> routes = null;
		List<SubServer> subServers = null;
		
		for (RouteFileParser parser : routeFileParsers) {
//			routes = parser.parse(vx);
			subServers = parser.parse(vx);
			if (subServers != null) {
				break;
			}
		}
		
//		VxMockRouteMatcher matcher = new VxMockRouteMatcher(vx) {
//			public void handle(HttpServerRequest request) {
//				System.out.println("Requested URL: " + request.path());
//				super.accept(request);
//			}
//		};
		
		Map<Integer, VxMockRouteMatcher> portsToMatchers = new HashMap<>();
		
		for (SubServer subServer : subServers) {
		    VxMockRouteMatcher matcher = new VxMockRouteMatcher(vx) {
	            public void handle(HttpServerRequest request) {
	                System.out.println("Requested URL: " + request.path());
	                super.accept(request);
	            }
	        };
	        for (RequestPath path : subServer.getRoutes().keySet()) {
	            RequestHandlerDelegate rhDelegate = new RequestHandlerDelegate();
	            rhDelegate.setRequestHandlers(subServer.getRoutes().get(path));
	            for (Method method : path.getMethods()) {
	                matcher.addRoute(path.getPath(), rhDelegate, method);
	            }
	        }
	        portsToMatchers.put(subServer.getPort(), matcher);
		}
		
		return portsToMatchers;
		
//		for (RequestPath path : routes.keySet()) {
//			RequestHandlerDelegate rhDelegate = new RequestHandlerDelegate();
//			rhDelegate.setRequestHandlers(routes.get(path));
//			for (Method method : path.getMethods()) {
//				matcher.addRoute(path.getPath(), rhDelegate, method);
//			}
//		}
//		
//		return matcher;
	}
	
	public static Handler<HttpServerRequest> handlerFor(final String filePath) {
		return new Handler<HttpServerRequest>() {
            public void handle(HttpServerRequest req) {
                System.out.println("Request for file: " + filePath + " based on path " + req.path());
                req.response().sendFile(filePath);
            }
        };
	}

}
