package com.taubler.vxmock.handlers;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import com.taubler.vxmock.io.RuntimeMessager;

/**
 * RequestHandler that sets the response's status code.
 * <br><br>
 * <b>Name:</b> setStatus
 * <br>
 * <b>Params:</b> 
 * <ul>
 * <li><i>status</i> (desired response status; e.g. 404)</li>
 * </ul>
 * <br>
 * <i>Example:</i>
 * <br>
 * <pre>
  {
    "route": "/data",
    "setStatus": {
      "status": 404
    }
  }
 * </pre>
 * @author dtaubler
 *
 */
public class SetStatusRequestHandler extends AbstractRequestHandler {
	
	private Integer status;
	
	public SetStatusRequestHandler() {
	}

	@Override
	public void handle(RoutingContext ctx) {
		HttpServerResponse resp = ctx.response();
		resp.setStatusCode(status);
		resp.end();

        RuntimeMessager.output("Setting status: " + status);
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
		if (status < 100 || status > 1000) {
	        RuntimeMessager.output("Response status " + status + " doesn't seem correct. Is this intentional?");
		}
	}

	
	@Override
	public int precedence() {
		return PRECENDENCE_FIRST;
	}

	@Override
	public String toString() {
		return String.format( "Set response status to %d ", status );
	}

}
