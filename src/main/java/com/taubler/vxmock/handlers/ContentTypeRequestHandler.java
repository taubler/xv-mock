package com.taubler.vxmock.handlers;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;

import com.taubler.vxmock.handlers.util.ParamUtil;
import com.taubler.vxmock.io.RuntimeMessager;
import com.taubler.vxmock.util.ReplaceableString;

public class ContentTypeRequestHandler extends AbstractRequestHandler {

	private ReplaceableString type;

	private ParamUtil paramUtil = new ParamUtil();
	
	public ContentTypeRequestHandler() {
	}

	@Override
	public void handle(RoutingContext ctx) {
		HttpServerRequest req = ctx.request();
		MultiMap params = req.params();
		Map<String, String> paramMap = paramUtil.multiMapToMap(params);
		String finalType = type.replace(paramMap);
		
        req.response().putHeader("Content-Type", finalType);

        RuntimeMessager.output("Setting content-type: " + type);
	}

	public String getType() {
		return type.toString();
	}

	public void setType(String type) {
		this.type = ReplaceableString.fromString(type);
	}

	
	@Override
	public int precedence() {
		return PRECENDENCE_FIRST;
	}

	@Override
	public String toString() {
		return "Set content-type to " + type.toString();
	}

}
