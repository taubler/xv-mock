package com.taubler.vxmock.handlers;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import com.taubler.vxmock.handlers.util.ParamUtil;
import com.taubler.vxmock.io.RuntimeMessager;
import com.taubler.vxmock.util.ReplaceableString;

/**
 * RequestHandler that sets a cookie in the response.
 * <br><br>
 * <b>Name:</b> setCookie
 * <br>
 * <b>Params:</b> 
 * <ul>
 * <li><i>name</i> (name of the cookie)</li>
 * <li><i>value</i> (value of the cookie)</li>
 * <li><i>path</i> (optional: root path to which the cookie is sent)</li>
 * <li><i>httpOnly</i> (optional; boolean)</li>
 * <li><i>age</i> (optional; number indicating milliseconds from now that the cookie will live)</li>
 * </ul>
 * <br>
 * <i>Example:</i>
 * <br>
 * <pre>
  {
    "route": "/data/:id",
    "setCookie": {
      "name": "sessionid",
      "value": "id-${id}",
      "path": "/",
      "httpOnly": true,
      "age": 345678
    }
  }
 * </pre>
 * @author dtaubler
 *
 */
public class SetCookieRequestHandler extends AbstractRequestHandler {

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
	public void handle(RoutingContext ctx) {
		HttpServerRequest req = ctx.request();
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
		if (path != null) {
			String finalPath = path.replace(paramMap);
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
