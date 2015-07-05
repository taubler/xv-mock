package com.taubler.vxmock.handlers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang.StringUtils;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServerRequest;

import com.taubler.vxmock.handlers.util.ValueHolder;
import com.taubler.vxmock.io.RuntimeMessager;

public class SetCookieRequestHandler implements RequestHandler {

	private String name;
	private String value;
	private String path;
	private Boolean httpOnly;
	private Long age;

	private static final String COOKIE_HEADER = "Set-Cookie";
	private static final String DATETIME_FORMAT = "EEE, dd MMM yyyy HH:MM:SS 'GMT'";
	
	public SetCookieRequestHandler() {
	}

	@Override
	public void handle(HttpServerRequest req) {
		ValueHolder valueHolder = new ValueHolder(value);
		MultiMap params = req.params();
		params.forEach(entry -> {
			valueHolder.value = StringUtils.replace(valueHolder.value, "${" + entry.getKey() + "}", entry.getValue());
		});
		
		StringBuilder cookieSb = new StringBuilder(); 
		cookieSb.append(name).append("=").append(valueHolder.value);
		appendAge(cookieSb);
		appendPath(cookieSb);
		appendHttpOnly(cookieSb);
		
        MultiMap headers = req.response().headers(); // Set-Cookie
        String existingCookies = headers.get(COOKIE_HEADER);
        String newCookie = cookieSb.toString();
        if (existingCookies == null) {
            req.response().putHeader(COOKIE_HEADER, newCookie); // TODO encode
        } else {
            req.response().putHeader(COOKIE_HEADER, existingCookies + "," + newCookie); // TODO encode
        }

        RuntimeMessager.output("Setting cookie: " + name + " = " + value);
	}
	
	public void appendHttpOnly(StringBuilder cookieSb) {
		if (httpOnly) {
			cookieSb.append("; HttpOnly");
		}
	}

	public void appendPath(StringBuilder cookieSb) {
		if (path != null) {
			cookieSb.append(";").append(" path=").append(path);
		}
	}

	public void appendAge(StringBuilder cookieSb) {
		if (age != null) {
			// use Java 8 Date API
			LocalDateTime expires = LocalDateTime.now().plusSeconds(age);
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATETIME_FORMAT);
			String expiresFormat = expires.format(dtf);
			cookieSb.append(";").append(" expires=").append(expiresFormat);
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
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Long getAge() {
		return age;
	}

	public void setAge(Long age) {
		this.age = age;
	}
	
	public Boolean getHttpOnly() {
		return httpOnly;
	}

	public void setHttpOnly(Boolean httpOnly) {
		this.httpOnly = httpOnly;
	}

	
	@Override
	public int precedence() {
		return PRECENDENCE_FIRST;
	}

}
