package com.taubler.vxmock.handlers;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;

import java.util.Map;

import com.taubler.vxmock.handlers.util.ParamUtil;
import com.taubler.vxmock.io.RuntimeMessager;
import com.taubler.vxmock.util.ReplaceableString;

public class RedirectRequestHandler extends AbstractRequestHandler {

	private ReplaceableString url;

	private static final String COOKIE_LOCATION = "Location";

	private ParamUtil paramUtil = new ParamUtil();
	
	public RedirectRequestHandler() {
	}

	@Override
	public void handle(HttpServerRequest req) {
		MultiMap params = req.params();
		Map<String, String> paramMap = paramUtil.multiMapToMap(params);
		String finalUrl = url.replace(paramMap);
		
        req.response().putHeader(COOKIE_LOCATION, finalUrl);
        req.response().setStatusCode(302);
        req.response().end();

        RuntimeMessager.output("Redirecting to: " + finalUrl);
	}
	
	public String getUrl() {
		return url.toString();
	}

	public void setUrl(String url) {
		this.url = ReplaceableString.fromString(url);
	}

	
	@Override
	public int precedence() {
		return PRECENDENCE_LAST;
	}

	@Override
	public String toString() {
		return "Redirect to " + url.toString();
	}

}
