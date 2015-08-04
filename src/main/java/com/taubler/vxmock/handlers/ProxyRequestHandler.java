package com.taubler.vxmock.handlers;


import org.vertx.java.core.MultiMap;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpServerRequest;

import com.taubler.vxmock.handlers.util.ParamUtil;
import com.taubler.vxmock.io.RuntimeMessager;
import com.taubler.vxmock.util.ReplaceableString;

public class ProxyRequestHandler implements RequestHandler {
	
	private ParamUtil paramUtil = new ParamUtil();
	private ReplaceableString host;
	private ReplaceableString path;
	private Integer port;
	
	public ProxyRequestHandler() {
	}
	
	public ProxyRequestHandler(String host) {
		this.host = ReplaceableString.fromString(host);
	}
	
	public ProxyRequestHandler(String host, String path) {
		this.host = ReplaceableString.fromString(host);
		this.path = ReplaceableString.fromString(path);
	}

	@Override
	public void handle(HttpServerRequest req) {
		MultiMap params = req.params();
		String finalHost = host.replace(paramUtil.multiMapToMap(params));
		String finalPath = path.replace(paramUtil.multiMapToMap(params));
		int finalPort = (port == null) ? 80 : port.intValue();
        RuntimeMessager.output("Request for to proxy response from: " + host + " based on host " + req.path());
        
        HttpClient httpc = VertxFactory.newVertx().createHttpClient();
        httpc.setPort(finalPort);
        httpc.setHost(finalHost);
    	HttpClientRequest cliReq = httpc.get(finalPath, resp -> {
    		RuntimeMessager.output("Got response of status " + resp.statusCode());
    		int status = resp.statusCode();
    		if (status == 200) {
	    		resp.bodyHandler(buff -> {
	    			String body = buff.toString();
	    			RuntimeMessager.output("Response body is " + body);
	    			req.response().setChunked(true);
	    			req.response().write(body);
	                req.response().end();
	    		});
    		} else {
    			req.response().setStatusCode(status);
                req.response().end();
    		}
    	});
    	cliReq.end();
	}

	public String getHost() {
		return host.toString();
	}

	public void setHost(String filePath) {
		this.host = ReplaceableString.fromString(filePath);
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
		return "Return response from  " + host + path + 
				( (port == null) ? "" : " (" + port.toString() + ")" );
	}

}
