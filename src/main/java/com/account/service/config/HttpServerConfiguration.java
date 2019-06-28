package com.account.service.config;

import static com.account.service.config.HttpProperty.*;

/**
 * Configuration for HTTP server's.
 */
public class HttpServerConfiguration {

    private static final int DEFAULT_THREADS = 100;
    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_SOCKET_BACKLOG = 0;
    private static final int DEFAULT_STOP_DELAY_SC = 0;

    public static final String CONTEXT_ROOT = "/api/account-service";

    /**
     * Count threads of web-server.
     */
    private final int threads;

    /**
     * HTTP port of web server.
     */
    private final int port;

    /**
     * Time which web server will be to wait when it will be stopping.
     */
    private final int stopDelay;
    private final int socketBacklog;

    public HttpServerConfiguration() {
        String threads = System.getProperty(HTTP_THREADS_PROPERTY);
        String port = System.getProperty(HTTP_PORT_PROPERTY);
        String socketBacklog = System.getProperty(HTTP_SOCKET_BACKLOG_PROPERTY);
        String stopDelay = System.getProperty(HTTP_SERVER_STOP_DELAY_PROPERTY);
        this.threads = threads != null ? Integer.valueOf(threads) : DEFAULT_THREADS;
        this.port = port != null ? Integer.valueOf(port) : DEFAULT_PORT;
        this.socketBacklog = socketBacklog != null ? Integer.valueOf(socketBacklog) : DEFAULT_SOCKET_BACKLOG;
        this.stopDelay = stopDelay != null ? Integer.valueOf(stopDelay) : DEFAULT_STOP_DELAY_SC;

    }

    public int getThreads() {
        return threads;
    }

    public int getPort() {
        return port;
    }

    public int getSocketBacklog() {
        return socketBacklog;
    }

    public int getStopDelay() {
        return stopDelay;
    }
}
