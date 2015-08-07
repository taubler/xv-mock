package com.taubler.vxmock;

public class LaunchContext {
	
	protected int port;
	private LaunchContext() {}
	
	protected static class Builder {
		private int port;
		protected Builder() {}
		protected Builder withPort(int port) {
			this.port = port;
			return this;
		}
		protected LaunchContext build() {
			LaunchContext ctx = new LaunchContext();
			ctx.port = port;
			return ctx;
		}
	}

	@Override
	public String toString() {
		return "LaunchContext [port=" + port + "]";
	}
	
}
