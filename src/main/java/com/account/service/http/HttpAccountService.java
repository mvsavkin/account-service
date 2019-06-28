package com.account.service.http;

import com.account.service.config.Configuration;
import com.account.service.config.HttpServerConfiguration;
import com.account.service.service.AccountService;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.account.service.config.HttpServerConfiguration.CONTEXT_ROOT;

public class HttpAccountService implements Server {

    private Configuration configuration;
    private AccountService accountService;
    private HttpServer httpServer;

    public HttpAccountService(Configuration configuration, AccountService accountService) {
        this.configuration = configuration;
        this.accountService = accountService;
    }

    public void initialize() throws Exception {
        HttpServerConfiguration httpServerConfiguration = configuration.getHttpServerConfiguration();
        ExecutorService executorService = Executors.newFixedThreadPool(httpServerConfiguration.getThreads());
        httpServer = HttpServer.create(new InetSocketAddress(httpServerConfiguration.getPort()),
                httpServerConfiguration.getSocketBacklog());
        httpServer.setExecutor(executorService);
        HttpContext context = httpServer.createContext(CONTEXT_ROOT);
        context.setHandler(new AccountServiceHttpHandler(accountService));
        context.setAuthenticator(new SimpleAuthenticator());
    }

    @Override
    public void start() {
        httpServer.start();
        System.out.println("Http server has been started...");
    }

    @Override
    public void stop() {
        httpServer.stop(configuration.getHttpServerConfiguration().getStopDelay());
        System.out.println("Http server has been stopped...");
    }
}
