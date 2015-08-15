package com.taubler.vxmock;

import com.taubler.vxmock.io.InitMessager;

public class LaunchContextFactory {
	
	private static final int MIN_PORT = 1;
	private static final int MAX_PORT = 65535;
		
	public static LaunchContext findLaunchContext() {
		InitMessager.output("Determining vx-mock's launch context...");

		int port = 8099;
		String portStr = System.getProperty("vxmock.port");
		if (portStr != null) {
			try {
				int suggPort = Integer.parseInt(portStr);
				if (suggPort >= MIN_PORT && suggPort <= MAX_PORT) {
					port = suggPort;
				} else {
					InitMessager.output( String.format("Could not use '%d' as a port (not between %d and %d); defaulting to %d\n", 
							suggPort, MIN_PORT, MAX_PORT, port) );
				}
			} catch (Exception e) {
				InitMessager.output( String.format("Could not use '%d' as a port; defaulting to %d\n", portStr, port) );
			}
			
		}
		LaunchContext ctx = new LaunchContext.Builder().withPort(port).build();
		InitMessager.output("vx-mock's launch context will be " + ctx);
		return ctx;
	}

}
