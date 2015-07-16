package com.taubler.vxmock.handlers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServerRequest;

import com.taubler.vxmock.handlers.util.ParamUtil;
import com.taubler.vxmock.io.RuntimeMessager;
import com.taubler.vxmock.util.ReplaceableString;

public class SetCookieRequestHandler implements RequestHandler {

	private ReplaceableString name;
	private ReplaceableString value;
	private ReplaceableString path;
	private Boolean httpOnly;
	private Long age;

	private static final String COOKIE_HEADER = "Set-Cookie";
	private static final String DATETIME_FORMAT = "EEE, dd MMM yyyy HH:MM:SS 'GMT'";

	private ParamUtil paramUtil = new ParamUtil();
	
	public SetCookieRequestHandler() {
	}

	@Override
	public void handle(HttpServerRequest req) {
		MultiMap params = req.params();
		Map<String, String> paramMap = paramUtil.multiMapToMap(params);
		String finalName = name.replace(paramMap);
		String finalValue = value.replace(paramMap);
		try {
			finalValue = URLEncoder.encode(finalValue, "UTF-8");
		} catch (UnsupportedEncodingException e) { }
		
		StringBuilder cookieSb = new StringBuilder(); 
		cookieSb.append(finalName).append("=").append(finalValue);
		appendAge(cookieSb);
		appendPath(cookieSb, paramMap);
		appendHttpOnly(cookieSb);
		
        MultiMap headers = req.response().headers(); // Set-Cookie
        String existingCookies = headers.get(COOKIE_HEADER);
        String newCookie = cookieSb.toString();
        if (existingCookies == null) {
            req.response().putHeader(COOKIE_HEADER, newCookie); // TODO encode
        } else {
            req.response().putHeader(COOKIE_HEADER, existingCookies + "," + newCookie); // TODO encode
        }

        RuntimeMessager.output("Setting cookie: " + finalName + " = " + finalValue);
	}
	
	public void appendHttpOnly(StringBuilder cookieSb) {
		if (httpOnly) {
			cookieSb.append("; HttpOnly");
		}
	}

	public void appendPath(StringBuilder cookieSb, Map<String, String> paramMap) {
		String finalPath = path.replace(paramMap);
		if (path != null) {
			cookieSb.append(";").append(" path=").append(finalPath);
		}
	}

	public void appendAge(StringBuilder cookieSb) {
		if (age != null) {
			LocalDateTime expires = LocalDateTime.now().plusSeconds(age);
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATETIME_FORMAT);
			String expiresFormat = expires.format(dtf);
			cookieSb.append(";").append(" expires=").append(expiresFormat);
		}
	}

	public String getName() {
		return name.toString();
	}

	public void setName(String cookieName) {
		this.name = ReplaceableString.fromString(cookieName);
	}

	public String getValue() {
		return value.toString();
	}

	public void setValue(String cookieValue) {
		this.value = ReplaceableString.fromString(cookieValue);
	}
	
	public String getPath() {
		return path.toString();
	}

	public void setPath(String path) {
		this.path = ReplaceableString.fromString(path);
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

	@Override
	public String toString() {
		return String.format( "Set cookie %s=%s, path:%s, age:currTime+%d seconds, httpOnly? %b", 
				name.toString(), value.toString(), path.toString(), age, httpOnly );
	}

}
