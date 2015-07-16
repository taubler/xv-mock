package com.taubler.vxmock.routes;


public class RequestPath  {
	
	public static enum Method {
		GET, POST, PUT, PATCH, HEAD
	}
	
	private String path;
	private Method[] methods = new Method[] { Method.GET };
	
	public RequestPath() {}
	
	public RequestPath(String path) {
		this.path = path;
	}
	
	public RequestPath(String path, Method... methods) {
		this.path = path;
		this.methods = methods;
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	public Method[] getMethods() {
		return methods;
	}
	public void setMethods(Method... methods) {
		this.methods = methods;
	}
	
	

}
