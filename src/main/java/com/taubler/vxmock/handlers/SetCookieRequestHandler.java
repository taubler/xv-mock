package com.taubler.vxmock.handlers;

import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServerRequest;

public class SetCookieRequestHandler implements RequestHandler {

	private String name;
	private String value;
	
	private static final String COOKIE_HEADER = "Set-Cookie";
	
	public SetCookieRequestHandler() {
	}

	@Override
	public void handle(HttpServerRequest req) {
        System.out.println("Setting cookie: " + name + " = " + value);
        MultiMap headers = req.response().headers(); // Set-Cookie
        String existingCookies = headers.get(COOKIE_HEADER);
        if (existingCookies == null) {
            req.response().putHeader(COOKIE_HEADER, name + "=" + value); // TODO encode
        } else {
            req.response().putHeader(COOKIE_HEADER, existingCookies + ";" + name + "=" + value); // TODO encode
        }
	}

	public String getName() {
		return name;
	}

	public void setName(String cookieName) {
		this.name = cookieName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String cookieValue) {
		this.value = cookieValue;
	}
	
	@Override
	public int precedence() {
		return PRECENDENCE_FIRST;
	}

}
