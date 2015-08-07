package com.taubler.vxmock.handlers;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		requestHandlers.forEach(rh -> sb.append("\n  - ").append(rh.toString()));
		return sb.toString();
	}
	
	Comparator<RequestHandler> handlerComparator = new Comparator<RequestHandler>() {

		@Override
		public int compare(RequestHandler o1, RequestHandler o2) {
			return o1.precedence() - o2.precedence();
		}
	};

}
