package com.taubler.vxmock.routes;

import io.vertx.core.Vertx;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.taubler.vxmock.handlers.FilePathRequestHandler;
import com.taubler.vxmock.handlers.RequestHandler;

public class TextRouteFileParser extends RouteFileParser {
	
	public static final String FILE_NAME = "routes.txt";

	@Override
	public String getFileName() {
		return FILE_NAME;
	}

	@Override
	protected void parseRoutes(File routeFile, Map<RequestPath, List<RequestHandler>> routes, Vertx vx) 
			throws Exception {
		List<String> lines = Files.readAllLines(routeFile.toPath(), Charset.defaultCharset());
		if (lines.size() % 2 != 0) {
			throw new RuntimeException(
					FILE_NAME + "needs to have an even number of lines, url-path then filepath, url-path then filepath, etc");
		} else {
			int cursor = 0;
			while (cursor < lines.size()) {
				String urlPath = lines.get(cursor++).trim();
				String filePath = lines.get(cursor++).trim();
				RequestPath rPath = new RequestPath(urlPath);
				routes.put(rPath, Arrays.asList( new FilePathRequestHandler(filePath) ));
			}
		}
	}

}
