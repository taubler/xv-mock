package com.taubler.vxmock.handlers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.taubler.vxmock.io.RuntimeMessager;
import com.taubler.vxmock.util.ReflectionUtil;

public class RequestHandlerFactory {

	private static final String FILE_PATH_REQUEST_HANDLER_KEY = "file";
	private static final Class<? extends RequestHandler> FILE_PATH_REQUEST_HANDLER_CLASS = FilePathRequestHandler.class;
	
	private static final String SET_COOKIE_REQUEST_HANDLER_KEY = "setcookie";
	private static final Class<? extends RequestHandler> SET_COOKIE_REQUEST_HANDLER_CLASS = SetCookieRequestHandler.class;
	
	private static final String CONTENT_TYPE_REQUEST_HANDLER_KEY = "contenttype";
	private static final Class<? extends RequestHandler> CONTENT_TYPE_REQUEST_HANDLER_CLASS = ContentTypeRequestHandler.class;
	
	private static final String HEADER_REQUEST_HANDLER_KEY = "header";
	private static final Class<? extends RequestHandler> HEADER_REQUEST_HANDLER_CLASS = SetHeaderRequestHandler.class;
	
	private static final String REDIRECT_REQUEST_HANDLER_KEY = "redirect";
	private static final Class<? extends RequestHandler> REDIRECT_REQUEST_HANDLER_CLASS = RedirectRequestHandler.class;
	
	private static final String PROXY_REQUEST_HANDLER_KEY = "proxy";
	private static final Class<? extends RequestHandler> PROXY_REQUEST_HANDLER_CLASS = ProxyRequestHandler.class;
	
	
	private static final Map<String, Class<? extends RequestHandler>> keysToClasses = 
			new HashMap<String, Class<? extends RequestHandler>>() {
				private static final long serialVersionUID = 1L;
				{
		put(FILE_PATH_REQUEST_HANDLER_KEY, FILE_PATH_REQUEST_HANDLER_CLASS);
		put(SET_COOKIE_REQUEST_HANDLER_KEY, SET_COOKIE_REQUEST_HANDLER_CLASS);
		put(CONTENT_TYPE_REQUEST_HANDLER_KEY, CONTENT_TYPE_REQUEST_HANDLER_CLASS);
		put(HEADER_REQUEST_HANDLER_KEY, HEADER_REQUEST_HANDLER_CLASS);
		put(REDIRECT_REQUEST_HANDLER_KEY, REDIRECT_REQUEST_HANDLER_CLASS);
		put(PROXY_REQUEST_HANDLER_KEY, PROXY_REQUEST_HANDLER_CLASS);
	}};
	
	public static RequestHandler getHandler(String name, Map<String, String> atts) {
		name = name.toLowerCase();
		Class<? extends RequestHandler> reqHandlerClass = keysToClasses.get(name);
		if (reqHandlerClass == null) {
			return null;
		}
		RequestHandler reqHandler = ReflectionUtil.create(reqHandlerClass);
		ReflectionUtil.merge(reqHandler, atts);
		return reqHandler;
	}
	
	public static List<RequestHandler> getHandlers(Map<String, Map<String, String>> handlerMappings) {
		List<RequestHandler> handlers = new LinkedList<>();
		for (String key : handlerMappings.keySet()) {
			RequestHandler rh = getHandler(key, handlerMappings.get(key));
			if (rh == null) {
				RuntimeMessager.output("Unable to create a request handler of name: " + key);
			} else {
				handlers.add(rh);
			}
		}
		return handlers;
	}

}
