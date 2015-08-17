package com.taubler.vxmock.handlers;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;

import com.taubler.vxmock.handlers.util.ParamUtil;
import com.taubler.vxmock.io.RuntimeMessager;
import com.taubler.vxmock.util.ReplaceableString;

public class SetHeaderRequestHandler extends AbstractRequestHandler {

	private ReplaceableString name;
	private ReplaceableString value;

	private ParamUtil paramUtil = new ParamUtil();
	
	public SetHeaderRequestHandler() {
	}

	@Override
	public void handle(RoutingContext ctx) {
		HttpServerRequest req = ctx.request();
		MultiMap params = req.params();
		Map<String, String> paramMap = paramUtil.multiMapToMap(params);
		String finalName = name.replace(paramMap);
		String finalValue = value.replace(paramMap);
		
        req.response().putHeader(finalName, finalValue);

        RuntimeMessager.output("Setting header: " + finalName + "+" + finalValue);
	}

	public String getName() {
		return name.toString();
	}

	public void setName(String name) {
		this.name = ReplaceableString.fromString(name);
	}

	public String getValue() {
		return value.toString();
	}

	public void setValue(String value) {
		this.value = ReplaceableString.fromString(value);
	}

	
	@Override
	public int precedence() {
		return PRECENDENCE_FIRST;
	}

	@Override
	public String toString() {
		return String.format( "Set response header %s=%s", name.toString(), value.toString() );
	}

}
