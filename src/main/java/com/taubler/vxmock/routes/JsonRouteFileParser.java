package com.taubler.vxmock.routes;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taubler.vxmock.handlers.RequestHandler;
import com.taubler.vxmock.handlers.RequestHandlerFactory;
import com.taubler.vxmock.io.RuntimeMessager;


public class JsonRouteFileParser extends RouteFileParser {

	public static final String FILE_NAME = "routes.json";
	public static final String ATTR_ROUTE = "route";
	public static final String ATTR_METHOD = "method";

	@Override
	protected void parseRoutes(File routeFile, Map<RequestPath, List<RequestHandler>> routes) throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(routeFile);
		if (jsonNode.isArray()) {
			jsonNode.forEach(routeActionsNode -> {
				JsonNode routeNode = routeActionsNode.get(ATTR_ROUTE);
				if (routeNode == null) {
					RuntimeMessager.output( 
							String.format("Came across a route without an attribute called '%'; check your % file",
									ATTR_ROUTE, FILE_NAME));
					return;
				}
				
				JsonNode methodNode = routeActionsNode.get(ATTR_METHOD);
				if (methodNode != null) {
					//TODO do something with this!!!
				}
				
				String route = routeNode.asText();
				Map<String, Map<String, String>> handlerMappings = captureHandlers(routeActionsNode);
				List<RequestHandler> handlers = RequestHandlerFactory.getHandlers(handlerMappings);
				RequestPath rPath = new RequestPath(route);
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
