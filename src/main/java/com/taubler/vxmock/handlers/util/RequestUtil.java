package com.taubler.vxmock.handlers.util;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.taubler.vxmock.io.RuntimeMessager;

public class RequestUtil {
	
	private ObjectMapper mapper = new ObjectMapper();

	public String serialize(HttpServerRequest request) {
		Map<String, Object> reqMap = new HashMap<>();
		reqMap.put("absoluteUri", request.absoluteURI());
		reqMap.put("form", request.formAttributes());
		reqMap.put("headers", request.headers());
		reqMap.put("localHost", request.localAddress().host());
		reqMap.put("localPort", request.localAddress().port());
		reqMap.put("method", request.method());
		reqMap.put("params", request.params());
		reqMap.put("path", request.path());
		reqMap.put("query", request.query());
		reqMap.put("remoteHost", request.remoteAddress().host());
		reqMap.put("remotePort", request.remoteAddress().port());
		reqMap.put("uri", request.uri());
		reqMap.put("httpVersion", request.version());
		
		String reqJson;
		try {
			reqJson = mapper.writeValueAsString(reqMap);
			RuntimeMessager.output("JSON = " + reqJson);
			return reqJson;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}
	
	public void transfer(RoutingContext ctx, String jsonStr) {
		HttpServerResponse resp = ctx.response();
		ObjectReader reader = mapper.reader(Map.class);
		try {
			Map<String, ? extends Object> m = reader.readValue(jsonStr);
			if (m.get("statusCode") != null) {
				resp.setStatusCode(getIntVal(m, "statusCode"));
			}
			if (m.get("headers") != null) {
				@SuppressWarnings("unchecked")
				Map<String, String> headers = (Map<String, String>)m.get("headers");
				headers.forEach((k, v) -> {
					resp.putHeader(k, v);
				});
			}
			if (m.get("cookies") != null) {
				@SuppressWarnings("unchecked")
				List<Map<String, String>> cookies = (List<Map<String, String>>)m.get("cookies");
				cookies.forEach(ckl -> {
					Cookie cookie = createCookie(ckl);
					ctx.addCookie(cookie);
				});
			}
			if (m.get("file") != null) {
				resp.sendFile((String)m.get("file"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Cookie createCookie(Map<String, String> values) {
		Cookie cookie = Cookie.cookie(values.get("name"), values.get("value"));
		if (values.get("httpOnly") != null) {
			cookie.setHttpOnly(Boolean.parseBoolean(values.get("httpOnly")));
		}
		if (values.get("secure") != null) {
			cookie.setSecure(Boolean.parseBoolean(values.get("secure")));
		}
		if (values.get("path") != null) {
			cookie.setPath(values.get("path"));
		}
		if (values.get("domain") != null) {
			cookie.setDomain(values.get("domain"));
		}
		return cookie;	
	}
	
	private int getIntVal(Map<String, ? extends Object> m, String key) {
		Object o = m.get(key);
		if (o == null) return 0;
		if (o instanceof Number) {
			return ((Number)o).intValue();
		}
		if (o instanceof String) {
			return Integer.parseInt((String)o);
		}
		return 0;
	}
	
}
