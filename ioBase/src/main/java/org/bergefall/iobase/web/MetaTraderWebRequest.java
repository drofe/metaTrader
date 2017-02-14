package org.bergefall.iobase.web;

import java.nio.charset.StandardCharsets;

import io.netty.handler.codec.http.FullHttpRequest;

/**
 * The Request class provides convenience helpers to the underlying
 * HTTP Request.
 */
public class MetaTraderWebRequest {
    private final FullHttpRequest request;


    /**
     * Creates a new Request.
     *
     * @param request The Netty HTTP request.
     */
    public MetaTraderWebRequest(final FullHttpRequest request) {
        this.request = request;
    }


    /**
     * Returns the body of the request.
     *
     * @return The request body.
     */
    public String body() {
        return request.content().toString(StandardCharsets.UTF_8);
    }
}