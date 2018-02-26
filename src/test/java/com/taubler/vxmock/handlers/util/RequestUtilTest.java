package com.taubler.vxmock.handlers.util;

import static com.taubler.vxmock.util.CollectionUtils.$;
import static com.taubler.vxmock.util.CollectionUtils.$l;
import static com.taubler.vxmock.util.CollectionUtils.$m;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import io.vertx.core.MultiMap;
import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public class RequestUtilTest {

	private RequestUtil requestUtil = new RequestUtil();
	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Test
	public void test_serialize() throws Exception {
		HttpServerRequest req = mock(HttpServerRequest.class);
		when(req.absoluteURI()).thenReturn("my.absolute.uri");
		MultiMap formMap = new CaseInsensitiveHeaders();
		formMap.add("username", "myusername");
		List<String> l = $l("Rob", "Bobby");
		formMap.add("nicknames", l);
		when(req.formAttributes()).thenReturn(formMap);
		
		String ser = requestUtil.serialize(req);
		ObjectReader reader = objectMapper.reader(Map.class);
		Map<String, ? extends Object> m = reader.readValue(ser);
		
		assertEquals("my.absolute.uri", m.get("absoluteUri"));
		assertNotNull(m.get("form"));
		Object o = m.get("form");
		assertTrue(o instanceof Map);
		assertEquals("myusername", ((Map)o).get("username"));
	}
	
	@Test
	public void test_transfer() throws Exception {
		HttpServerRequest req = mock(HttpServerRequest.class);
		HttpServerResponse resp = mock(HttpServerResponse.class);
		RoutingContext ctx = mock(RoutingContext.class);
		when(ctx.request()).thenReturn(req);
		when(ctx.response()).thenReturn(resp);
		
		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("statusCode", 400);
		jsonMap.put("file", "/path/to/my.file");
		jsonMap.put("headers", $m( $("X-Foo", "bar"), $("X-Donkey", "monkey") ));
		jsonMap.put("cookies", $l( $m( $("name", "type"), $("value", "chocolate-chip")) ));
		
		String jsonStr = objectMapper.writeValueAsString(jsonMap);
		requestUtil.transfer(ctx, jsonStr);

		Mockito.verify(resp).setStatusCode(400);
		Mockito.verify(resp).sendFile("/path/to/my.file");
		Mockito.verify(resp).putHeader("X-Foo", "bar");
		Mockito.verify(resp).putHeader("X-Donkey", "monkey");
		Mockito.verify(resp, Mockito.times(2)).putHeader(Matchers.anyString(), Matchers.anyString());
		Mockito.verify(ctx).addCookie(Matchers.any(Cookie.class));
		Mockito.verify(ctx, Mockito.times(1)).addCookie(Matchers.any(Cookie.class));
	}
	
	@Test
	public void testCreateCookie_minimal() {
		Map<String, String> values = new HashMap<>();
		values.put("name", "myname");
		values.put("value", "myvalue");
		Cookie cookie = requestUtil.createCookie(values);
		assertEquals("myname", cookie.getName());
		assertEquals("myvalue", cookie.getValue());
		assertNull(cookie.getDomain());
		assertNull(cookie.getPath());
	}
	
	@Test
	public void testCreateCookie_full() {
		Map<String, String> values = new HashMap<>();
		values.put("name", "myname");
		values.put("value", "myvalue");
		values.put("httpOnly", "true");
		values.put("secure", "true");
		values.put("path", "/mypath");
		values.put("domain", "www.mydomain.com");
		Cookie cookie = requestUtil.createCookie(values);
		assertEquals("myname", cookie.getName());
		assertEquals("myvalue", cookie.getValue());
		assertEquals("www.mydomain.com", cookie.getDomain());
		assertEquals("/mypath", cookie.getPath());
		// can't test any other values directly, unfortunately...
	}

}
