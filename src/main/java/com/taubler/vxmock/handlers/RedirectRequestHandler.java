package com.taubler.vxmock.handlers;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;

import com.taubler.vxmock.handlers.util.ParamUtil;
import com.taubler.vxmock.io.RuntimeMessager;
import com.taubler.vxmock.util.ReplaceableString;

/**
 * RequestHandler that sends a 302 redirect in the response.
 * <br><br>
 * <b>Name:</b> redirect
 * <br>
 * <b>Params:</b> 
 * <ul>
 * <li><i>url</i> (in a form like "application/json")</li>
 * </ul>
 * <br>
 * <i>Example:</i>
 * <br>
 * <pre>
  {
    "route": "/redirect",
    "redirect": {
      "url": "http://www.yahoo.com"
    }
  }
 * </pre>
 * @author dtaubler
 *
 */
public class RedirectRequestHandler extends AbstractRequestHandler {

	private ReplaceableString url;

	private static final String COOKIE_LOCATION = "Location";

	private ParamUtil paramUtil = new ParamUtil();
	
	public RedirectRequestHandler() {
	}

	@Override
	public void handle(RoutingContext ctx) {
		HttpServerRequest req = ctx.request();
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
