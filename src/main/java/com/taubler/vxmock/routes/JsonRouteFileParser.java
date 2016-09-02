package com.taubler.vxmock.routes;

import io.vertx.core.Vertx;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taubler.vxmock.handlers.RequestHandler;
import com.taubler.vxmock.handlers.RequestHandlerFactory;
import com.taubler.vxmock.io.RuntimeMessager;
import com.taubler.vxmock.routes.RequestPath.Method;


public class JsonRouteFileParser extends RouteFileParser {

	public static final String FILE_NAME = "routes.json";
	
	public static final String ATTR_PORT = "port";
    public static final String ATTR_ROUTES = "routes";
    public static final String ATTR_ROUTE = "route";
	public static final String ATTR_METHOD = "method";

    ObjectMapper objectMapper = new ObjectMapper();

	@Override
	protected void parseRoutes(File routeFile, /*Map<RequestPath, List<RequestHandler>>*/ List<SubServer> subServers, Vertx vx) 
			throws Exception {
		JsonNode topJsonNode = objectMapper.readTree(routeFile);
		if (topJsonNode.isArray()) {
		    topJsonNode.forEach(subServerNode -> {
		        if (subServerNode.isObject()) {
		            JsonNode portNode = subServerNode.get(ATTR_PORT);
		            int port = portNode.asInt(); // defaults to 0 if not really an int
		            Map<RequestPath, List<RequestHandler>> routesMap = new HashMap<>();
		            JsonNode routesNode = subServerNode.get(ATTR_ROUTES);
		            parseRoutes(routesMap, vx, routesNode);
		            ensureRoutesMapToSubServer(port, routesMap, subServers);
		        }
		    });
		}
	}
	
	private void ensureRoutesMapToSubServer(int port, Map<RequestPath, List<RequestHandler>> routesMap, List<SubServer> subServers) {
	    SubServer subServer = subServers.stream().filter(ss -> {return ss.getPort() == port;}).findAny().orElseGet(() -> {
    	        SubServer ss = new SubServer(port);
    	        subServers.add(ss);
    	        return ss;
	        });
	    subServer.addRoutes(routesMap);
	}

    private void parseRoutes(Map<RequestPath, List<RequestHandler>> routes, Vertx vx, JsonNode arrayJsonNode) {
        if (arrayJsonNode.isArray()) {
			arrayJsonNode.forEach(routeActionsNode -> {
				JsonNode routeNode = routeActionsNode.get(ATTR_ROUTE);
				if (routeNode == null) {
					RuntimeMessager.output( 
							String.format("Came across a route without an attribute called '%'; check your % file",
									ATTR_ROUTE, FILE_NAME));
					return;
				}
				
				JsonNode methodNode = routeActionsNode.get(ATTR_METHOD);
				List<RequestPath.Method> matchingMethods = new LinkedList<>();
				if (methodNode != null) {
					if (methodNode.isArray()) {
						methodNode.forEach(method -> {
							String thisMethodName = method.asText();
							Method m = Method.valueOf(thisMethodName);
							matchingMethods.add(m);
						});
					} else {
						String thisMethodName = methodNode.asText();
						Method m = Method.valueOf(thisMethodName);
						matchingMethods.add(m);
					}
				}
				
				String route = routeNode.asText();
				Map<String, Map<String, String>> handlerMappings = captureHandlers(routeActionsNode);
				List<RequestHandler> handlers = RequestHandlerFactory.getHandlers(handlerMappings, vx);
				RequestPath rPath = new RequestPath(
						route, matchingMethods.toArray(new Method[matchingMethods.size()]));
				routes.put(rPath, handlers);
			});
		}
    }

	public Map<String, Map<String, String>> captureHandlers(JsonNode jsonNode) {
		Map<String, Map<String, String>> entries = new HashMap<>();
		jsonNode.fields().forEachRemaining(e1 -> {
			JsonNode mapNode = e1.getValue();
			String key = e1.getKey();
			if (ATTR_ROUTE.equals(key) || ATTR_METHOD.equals(key))
				return;
			
			Map<String, String> values = new HashMap<>();
			mapNode.fields().forEachRemaining(e2 -> {
//				JsonNodeType nodeType = e2.getValue().getNodeType();
//				switch (nodeType) {
//				case STRING:
					values.put(e2.getKey(), e2.getValue().asText());
//					break;
//				case NUMBER:
//					values.put(e2.getKey(), e2.getValue().asInt());
//					break;
//				case BOOLEAN:
//					values.put(e2.getKey(), e2.getValue().asBoolean());
//					break;
//				default:
//					values.put(e2.getKey(), e2.getValue().asText());
//					break;
//				}
			});
			entries.put(key, values);
		});
		
		return entries;
	}

	@Override
	public String getFileName() {
		return FILE_NAME;
	}

}
