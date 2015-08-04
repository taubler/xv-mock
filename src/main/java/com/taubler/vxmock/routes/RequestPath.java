package com.taubler.vxmock.routes;


public class RequestPath  {
	
	public static enum Method {
		GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS, CONNECT, TRACE, _ANY;
	}
	
	private String path;
	private Method[] methods = new Method[] { Method._ANY };
	
	public RequestPath() {}
	
	public RequestPath(String path) {
		this.path = path;
	}
	
	public RequestPath(String path, Method... methods) {
		this.path = path;
		if (methods != null && methods.length > 0) {
			this.methods = methods;
		}
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
