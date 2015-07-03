package com.taubler.vxmock.handlers;

import org.vertx.java.core.http.HttpServerRequest;

import com.taubler.vxmock.io.RuntimeMessager;

public class FilePathRequestHandler implements RequestHandler {
	
	private String path;
	
	public FilePathRequestHandler() {
	}
	
	public FilePathRequestHandler(String filePath) {
		this.path = filePath;
	}

	@Override
	public void handle(HttpServerRequest req) {
        RuntimeMessager.output("Request for file: " + path + " based on path " + req.path());
        req.response().sendFile(path);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String filePath) {
		this.path = filePath;
	}
	
	@Override
	public int precedence() {
		return PRECENDENCE_LAST;
	}

}
