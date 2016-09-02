package com.taubler.vxmock.routes;

import io.vertx.core.Vertx;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.taubler.vxmock.io.InitMessager;

public abstract class RouteFileParser {
	
	public /*Map<RequestPath, List<RequestHandler>>*/ List<SubServer> parse(Vertx vx) throws Exception {
		
		String fileName = getFileName();
		File routeFile = new File( fileName );
		
		if (routeFile.exists()) {
			
			InitMessager.output("Reading routes file: " + routeFile);
//			Map<RequestPath, List<RequestHandler>> routes = new HashMap<>();
			List<SubServer> subServers = new ArrayList<>();
			try {
				parseRoutes(routeFile, subServers, vx);
				return subServers;
			} catch (Throwable t) {
				InitMessager.output( 
						String.format("Unable to construct routes from file %s; %s", fileName, t.getMessage()) );
				return null;
			}
			
		} else {
			
			return null;
			
		}

	}

	protected abstract void parseRoutes(File routeFile, /*Map<RequestPath, List<RequestHandler>>*/ List<SubServer> routes, Vertx vx) 
			throws Exception;
	
	public abstract String getFileName();

}
