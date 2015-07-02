package com.taubler.vxmock.routes;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.taubler.vxmock.io.InitMessager;

public abstract class RouteFileParser {
	
	public Map<String, String> parse() throws Exception {
		
		String fileName = getFileName();
		File routeFile = new File( fileName );
		
		if (routeFile.exists()) {
			
			InitMessager.output("Reading routes file: " + routeFile);
			Map<String, String> routes = new HashMap<String, String>();
			try {
				parseRoutes(routeFile, routes);
				return routes;
			} catch (Throwable t) {
				InitMessager.output( 
						String.format("Unable to construct routes from file %s; %s", fileName, t.getMessage()) );
				return null;
			}
			
		} else {
			
			return null;
			
		}

	}

	protected abstract void parseRoutes(File routeFile, Map<String, String> routes) throws Exception;
	
	public abstract String getFileName();

}
