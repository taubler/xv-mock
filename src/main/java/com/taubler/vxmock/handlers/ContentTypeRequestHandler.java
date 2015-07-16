package com.taubler.vxmock.handlers;

import java.util.Map;

import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServerRequest;

import com.taubler.vxmock.handlers.util.ParamUtil;
import com.taubler.vxmock.io.RuntimeMessager;
import com.taubler.vxmock.util.ReplaceableString;

public class ContentTypeRequestHandler implements RequestHandler {

	private ReplaceableString type;

	private ParamUtil paramUtil = new ParamUtil();
	
	public ContentTypeRequestHandler() {
	}

	@Override
	public void handle(HttpServerRequest req) {
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
