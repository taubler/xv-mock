package com.taubler.vxmock.util;

public class Pair<X , Y> { 
	
	public final X x; 
	public final Y y; 
	
	public Pair(X x, Y y) { 
		this.x = x; 
		this.y = y; 
	} 
	
	protected Pair() {
	    this.x = null;
	    this.y = null;
	}
	
} 
