package com.taubler.vxmock.handlers;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;

public class RequestHandlerDelegate implements Handler<HttpServerRequest> {
	
	private List<RequestHandler> requestHandlers;

	@Override
	public void handle(HttpServerRequest event) {
		requestHandlers.forEach(rh -> rh.handle(event));
	}
	
	public void setRequestHandlers(List<RequestHandler> requestHandlers) {
		this.requestHandlers = requestHandlers;
		Collections.sort(this.requestHandlers, handlerComparator);
	}
	
	Comparator<RequestHandler> handlerComparator = new Comparator<RequestHandler>() {

		@Override
		public int compare(RequestHandler o1, RequestHandler o2) {
			return o1.precedence() - o2.precedence();
		}
	};

}
