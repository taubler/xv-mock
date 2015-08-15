package com.taubler.vxmock.handlers;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taubler.vxmock.handlers.util.ParamUtil;
import com.taubler.vxmock.io.RuntimeMessager;
import com.taubler.vxmock.util.ReplaceableString;

public class VerticleRequestHandler extends AbstractRequestHandler {
	
	private ParamUtil paramUtil = new ParamUtil();
	private ReplaceableString verticle;
	private ReplaceableString address;
	
	public VerticleRequestHandler() {
	}
	
	public VerticleRequestHandler(String verticle, String address) {
		this.verticle = ReplaceableString.fromString(verticle);
		this.address = ReplaceableString.fromString(address);
	}

	@Override
	public void handle(HttpServerRequest req) {
		MultiMap params = req.params();
		String finalVerticle = verticle.replace(paramUtil.multiMapToMap(params));
		String finalAddress = address.replace(paramUtil.multiMapToMap(params));
		
		vertx.deployVerticle("./" + finalVerticle, res -> {
			if (res.succeeded()) {
				String deploymentId = res.result();
				ObjectMapper mapper = new ObjectMapper();
				String reqJson;
				try {
					reqJson = mapper.writeValueAsString(req);
					RuntimeMessager.output("JSON = " + reqJson);
					vertx.eventBus().send(finalAddress, reqJson, rh -> {
						vertx.undeploy(deploymentId);
					});
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				req.response().end();
			} else {
				RuntimeMessager.output("Error deploying verticle " + finalVerticle + 
						"; cwd = " + System.getProperty("user.dir") + "; cause = " + res.cause());
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
