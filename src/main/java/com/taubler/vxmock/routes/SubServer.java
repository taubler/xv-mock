package com.taubler.vxmock.routes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.taubler.vxmock.handlers.RequestHandler;

public class SubServer {
    
    private int port;
    private Map<RequestPath, List<RequestHandler>> routes = new HashMap<>();
    

    public SubServer() {
    }
    
    public SubServer(int port) {
        super();
        this.port = port;
    }
    
    public SubServer(int port, Map<RequestPath, List<RequestHandler>> routes) {
        super();
        this.port = port;
        this.routes = routes;
    }
    
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    
    public Map<RequestPath, List<RequestHandler>> getRoutes() {
        return routes;
    }
    public void addRoutes(Map<RequestPath, List<RequestHandler>> newRoutes) {
        if (newRoutes != null) {
            newRoutes.forEach((k,v) -> { routes.put(k, v); });
        }
    }
    
}
