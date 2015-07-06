package com.taubler.vxmock.handlers.util;

import java.util.HashMap;
import java.util.Map;

import org.vertx.java.core.MultiMap;

public class ParamUtil {
	
	public Map<String, String> multiMapToMap(MultiMap mm) {
		Map<String, String> m = new HashMap<>();
		mm.forEach(e -> m.put(e.getKey(), e.getValue()));
		return m;
	}

}
