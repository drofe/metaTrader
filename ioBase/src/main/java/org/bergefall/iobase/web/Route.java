package org.bergefall.iobase.web;

import io.netty.handler.codec.http.HttpMethod;

/**
 * The Route class represents a single entry in the RouteTable.
 */
public class Route {
    private final HttpMethod method;
    private final String path;
    private final WebReqHandler handler;

    public Route(final HttpMethod method, final String path, final WebReqHandler handler) {
        this.method = method;
        this.path = path;
        this.handler = handler;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public WebReqHandler getHandler() {
        return handler;
    }

    public boolean matches(final HttpMethod method, final String path) {
        return this.method.equals(method) && this.path.equals(path);
    }
}