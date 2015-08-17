package com.taubler.vxmock.handlers;


import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

import com.taubler.vxmock.handlers.util.ParamUtil;
import com.taubler.vxmock.io.RuntimeMessager;
import com.taubler.vxmock.util.ReplaceableString;

/**
 * RequestHandler that allows the proxying of a request to another URL.
 * In other words, this handler will return the contents that are returned
 * by the given URL.
 * <br><br>
 * <b>Name:</b> route
 * <br>
 * <b>Params:</b> 
 * <ul>
 * <li><i>host</i> (in a form like "www.host.com")</li>
 * <li><i>path</i></li>
 * <li><i>port</i> (optional; defaults to 80)</li>
 * </ul>
 * <br>
 * <i>Example:</i>
 * <br>
 * <pre>
  {
    "route": "/proxy/:file",
    "proxy": {
      "host": "www.somehost.com",
      "path": "/folder/${file}",
      "port": 8080
  }
 * </pre>
 * @author dtaubler
 *
 */
public class ProxyRequestHandler extends AbstractRequestHandler {
	
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
	public void handle(RoutingContext ctx) {
		HttpServerRequest req = ctx.request();
		MultiMap params = req.params();
		String finalHost = host.replace(paramUtil.multiMapToMap(params));
		String finalPath = path.replace(paramUtil.multiMapToMap(params));
		int finalPort = (port == null) ? 80 : port.intValue();
        RuntimeMessager.output("Request for to proxy response from: " + host + " based on host " + req.path());
        
        HttpClient httpc = vertx.createHttpClient();
    	HttpClientRequest cliReq = httpc.get(finalPort, finalHost, finalPath, resp -> {
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
