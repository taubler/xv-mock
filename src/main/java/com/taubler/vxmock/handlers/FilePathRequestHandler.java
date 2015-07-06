package com.taubler.vxmock.handlers;

import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServerRequest;

import com.taubler.vxmock.handlers.util.ParamUtil;
import com.taubler.vxmock.io.RuntimeMessager;
import com.taubler.vxmock.util.ReplaceableString;

public class FilePathRequestHandler implements RequestHandler {
	
	private ParamUtil paramUtil = new ParamUtil();
	private ReplaceableString path;
	
	public FilePathRequestHandler() {
	}
	
	public FilePathRequestHandler(String filePath) {
		this.path = ReplaceableString.fromString(filePath);
	}

	@Override
	public void handle(HttpServerRequest req) {
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

}
