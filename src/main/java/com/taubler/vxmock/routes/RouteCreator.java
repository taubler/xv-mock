package com.taubler.vxmock.routes;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;

import com.taubler.vxmock.handlers.RequestHandler;
import com.taubler.vxmock.handlers.RequestHandlerDelegate;
import com.taubler.vxmock.routes.RequestPath.Method;

public class RouteCreator {
	
	private static final List<? extends RouteFileParser> routeFileParsers = 
			Arrays.asList(new JsonRouteFileParser()
//			, new TextRouteFileParser()
			);
	
	public VxMockRouteMatcher createRoutes() throws Exception {
		
		Map<RequestPath, List<RequestHandler>> routes = null;
		
		for (RouteFileParser parser : routeFileParsers) {
			routes = parser.parse();
			if (routes != null) {
				break;
			}
		}
		
		VxMockRouteMatcher matcher = new VxMockRouteMatcher() {
			public void handle(HttpServerRequest request) {
				System.out.println("Requested URL: " + request.path());
				super.handle(request);
			}
		};
		
		for (RequestPath path : routes.keySet()) {
			RequestHandlerDelegate rhDelegate = new RequestHandlerDelegate();
			rhDelegate.setRequestHandlers(routes.get(path));
			for (Method method : path.getMethods()) {
				matcher.addRoute(path.getPath(), rhDelegate, method);
			}
		}
		
		return matcher;
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
