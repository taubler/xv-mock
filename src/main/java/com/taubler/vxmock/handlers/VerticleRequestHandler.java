package com.taubler.vxmock.handlers;

import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

import com.taubler.vxmock.handlers.util.ParamUtil;
import com.taubler.vxmock.handlers.util.RequestUtil;
import com.taubler.vxmock.io.RuntimeMessager;
import com.taubler.vxmock.util.ReplaceableString;

public class VerticleRequestHandler extends AbstractRequestHandler {
	
	private ParamUtil paramUtil = new ParamUtil();
	private RequestUtil requestUtil = new RequestUtil();
	
	private ReplaceableString verticle;
	private ReplaceableString address;
	
	public VerticleRequestHandler() {
	}
	
	public VerticleRequestHandler(String verticle, String address) {
		this.verticle = ReplaceableString.fromString(verticle);
		this.address = ReplaceableString.fromString(address);
	}

	@Override
	public void handle(RoutingContext ctx) {
		HttpServerRequest req = ctx.request();
		MultiMap params = req.params();
		String finalVerticle = verticle.replace(paramUtil.multiMapToMap(params));
		String finalAddress = address.replace(paramUtil.multiMapToMap(params));
		
		vertx.deployVerticle("./" + finalVerticle, result -> {
			if (result.succeeded()) {
				String deploymentId = result.result();
				vertx.eventBus().send(finalAddress, requestUtil.serialize(req), rh -> {
					Message<Object> msg = rh.result();
					requestUtil.transfer(ctx, (String)msg.body());
					vertx.undeploy(deploymentId);
					req.response().end();
				});
			} else {
				RuntimeMessager.output("Error deploying verticle " + finalVerticle + 
						"; cwd = " + System.getProperty("user.dir") + "; cause = " + result.cause());
				req.response().setStatusCode(500);
				req.response().end();
			}
		});
		
	}
	
	public ReplaceableString getVerticle() {
		return verticle;
	}

	public void setVerticle(String verticle) {
		this.verticle = ReplaceableString.fromString(verticle);
	}

	public ReplaceableString getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = ReplaceableString.fromString(address);
	}
	

	@Override
	public int precedence() {
		return PRECENDENCE_LAST;
	}

	@Override
	public String toString() {
		return "Let verticle  " + verticle + " handle request";
	}

}
