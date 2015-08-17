package com.taubler.vxmock.handlers;


import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

import com.taubler.vxmock.handlers.util.ParamUtil;
import com.taubler.vxmock.io.RuntimeMessager;
import com.taubler.vxmock.util.ReplaceableString;

public class FilePathRequestHandler extends AbstractRequestHandler {
	
	private ParamUtil paramUtil = new ParamUtil();
	private ReplaceableString path;
	
	public FilePathRequestHandler() {
	}
	
	public FilePathRequestHandler(String filePath) {
		this.path = ReplaceableString.fromString(filePath);
	}

	@Override
	public void handle(RoutingContext ctx) {
		HttpServerRequest req = ctx.request();
		MultiMap params = req.params();
		String finalPath = path.replace(paramUtil.multiMapToMap(params));
        RuntimeMessager.output("Request for file: " + path + " based on path " + req.path());
        req.response().sendFile(finalPath);
	}

	public String getPath() {
		return path.toString();
	}

	public void setPath(String filePath) {
		this.path = ReplaceableString.fromString(filePath);
	}
	
	@Override
	public int precedence() {
		return PRECENDENCE_LAST;
	}

	@Override
	public String toString() {
		return "Return file " + path.toString();
	}

}
