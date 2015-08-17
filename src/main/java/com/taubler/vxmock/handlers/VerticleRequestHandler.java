package com.taubler.vxmock.handlers;

import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

import com.taubler.vxmock.handlers.util.ParamUtil;
import com.taubler.vxmock.handlers.util.RequestUtil;
import com.taubler.vxmock.io.RuntimeMessager;
import com.taubler.vxmock.util.ReplaceableString;

/**
 * <p>RequestHandler that delegates to a separate vert.x verticle.</p>
 * <p>The verticle can (in theory, anyway) be written in any supported language,
 * and should be located in a file that's accessible by the server.</p>
 * <p>The verticle should subscribe as a consumer to the event bus, listening on the address
 * specified in the <i>address</i> param. A Javascript example would be:
 * <pre>
  eb.consumer('my.address', function(msg) {
    var req = msg.body();
    var ret = '{"statusCode": 201, "headers":{"foo":"bar"}';
    ret = ret + ', "cookies":[{"name":"type", "value":"chocolate-chip", "path":"/"}]}';
    msg.reply(ret);
  });
 * </pre>
 * </p>The consumer callback function will receive a JSON string containing information
 * about the request; for example:
 * <pre>
{
  "headers":{"empty":false},
  "localHost":"127.0.0.1",
  "localPort":8099,
  "remotePort":51580,
  "remoteHost":"127.0.0.1",
  "method":"GET",
  "query":"qs1=value1&qs2=value2",
  "absoluteUri":"http://localhost:8099/verticle",
  "params":{"empty":true},
  "uri":"/verticle",
  "path":"/verticle",
  "httpVersion":"HTTP_1_1",
  "form":{"empty":true}
}
 * </pre>
 * <p>
 * The verticle should send some response back. The response can contain instructions 
 * for the HttpServerResponse sent back to the browser. For that, a JSON string should
 * be returned; possible params are: 
 * <ul>
 * <li><i>statusCode</i>: numeric value representing the HTTP status code to return; e.g. 400</li>
 * <li><i>file</i>: path to a file to be returned as the body of the response</li>
 * <li><i>headers</i>: map of key/value pairs to be sent as response headers</li>
 * <li><i>cookies</i>: list of cookies; each cookie contains "name" and "value", 
 * and optionally "httpOnly", "path", "secure", and "domain".</li>
 * </ul>
 * </p>
 * <br>
 * <b>Name:</b> verticle
 * <br>
 * <b>Params:</b> 
 * <ul>
 * <li><i>address</i> (event bus address to which the verticle will subscribe)</li>
 * <li><i>verticle</i> (path to the verticle file)</li>
 * </ul>
 * <br>
 * <i>Example:</i>
 * <br>
 * <pre>
  {
    "route": "/verticle",
    "verticle": {
      "address": "my.address",
      "verticle": "myverticle.js"
    }
  }
 * </pre>
 * @author dtaubler
 *
 */
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
