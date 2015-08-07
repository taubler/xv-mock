package com.taubler.vxmock.handlers.util;

import io.vertx.core.MultiMap;

import java.util.HashMap;
import java.util.Map;

public class ParamUtil {
	
	public Map<String, String> multiMapToMap(MultiMap mm) {
		Map<String, String> m = new HashMap<>();
		mm.forEach(e -> m.put(e.getKey(), e.getValue()));
		return m;
	}

}
