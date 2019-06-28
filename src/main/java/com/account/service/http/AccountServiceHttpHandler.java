package com.account.service.http;

import com.account.service.dto.AccountDTO;
import com.account.service.dto.TransferDTO;
import com.account.service.exceptions.AccountNotFoundException;
import com.account.service.exceptions.InsufficientMoneyException;
import com.account.service.exceptions.InternalRepositoryException;
import com.account.service.serialization.JsonSerializer;
import com.account.service.serialization.Serializer;
import com.account.service.service.AccountService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.account.service.config.HttpServerConfiguration.CONTEXT_ROOT;

public class AccountServiceHttpHandler implements HttpHandler {

    private static final Serializer<TransferDTO> SERIALIZER = new JsonSerializer<>(TransferDTO.class);
    private static final Serializer<AccountDTO> SERIALIZER_ACCOUNT = new JsonSerializer<>(AccountDTO.class);

    private final AccountService accountService;

    public AccountServiceHttpHandler(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        String fullPath = httpExchange.getRequestURI().getPath();
        String path = fullPath.replace(CONTEXT_ROOT, "");
        if ("/transfer".equals(path)) {
            processTransferRequest(httpExchange);
            return;
        }

        if (path.startsWith("/findById")) {
            processFindById(httpExchange, path);
            return;
        }

        respondNotFound(httpExchange, fullPath);
    }

    private void processFindById(HttpExchange httpExchange, String path) {
        String id = path.replace("/findById/", "");
        try {
            try {
                if ("GET".equals(httpExchange.getRequestMethod())) {
                    long accountId = Long.parseLong(id);
                    AccountDTO account = accountService.findById(accountId);
                    byte[] payload = SERIALIZER_ACCOUNT.serialize(account);
                    httpExchange.sendResponseHeaders(HttpStatus.OK.value(), payload.length);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(payload);
                    }
                } else {
                    String response = "GET only supported";
                    respondBadRequest(httpExchange, response);
                }
            } catch (AccountNotFoundException e) {
                e.printStackTrace();
                respondError(httpExchange, e, HttpStatus.ACCOUNT_NOT_FOUND);
            } catch (InternalRepositoryException e) {
                e.printStackTrace();
                respondError(httpExchange, e, HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (Exception e) {
                e.printStackTrace();
                respondBadRequest(httpExchange, "Bad request.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void processTransferRequest(HttpExchange httpExchange) {
        InputStream requestBody = httpExchange.getRequestBody();
        String requestMethod = httpExchange.getRequestMethod();
        try {
            if ("POST".equals(requestMethod)) {
                byte[] bytes = readFully(requestBody);
                TransferDTO transferDTO = SERIALIZER.deserialize(bytes);
                processRequestMessage(httpExchange, transferDTO);
            } else {
                String response = "POST only supported";
                respondBadRequest(httpExchange, response);
            }
        }  catch (Exception e) {
            e.printStackTrace();
            respondBadRequest(httpExchange, "Bad request.");
        }
    }

    private void processRequestMessage(HttpExchange httpExchange, TransferDTO transferDTO) throws IOException {
        try {
            accountService.transfer(transferDTO);
            String response = "Success transfer money.";
            byte[] payload = response.getBytes();
            httpExchange.sendResponseHeaders(HttpStatus.OK.value(), payload.length);
            try(OutputStream os = httpExchange.getResponseBody()) {
                os.write(payload);
            }
        } catch (AccountNotFoundException e){
            respondError(httpExchange, e,  HttpStatus.ACCOUNT_NOT_FOUND);
        } catch (InsufficientMoneyException e) {
            respondError(httpExchange, e,  HttpStatus.INSUFFICIENT_MONEY);
        } catch (Exception e) {
            respondError(httpExchange, e,  HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void respondBadRequest(HttpExchange httpExchange, String response) {
        try {
            byte[] payload = response.getBytes();
            httpExchange.sendResponseHeaders(HttpStatus.BAD_REQUEST.value(), payload.length);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(payload);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void respondError(HttpExchange httpExchange, Exception e, HttpStatus internalServerError) throws IOException {
        e.printStackTrace();
        String response = "Error: " + e.getMessage();
        byte[] payload = response.getBytes();
        httpExchange.sendResponseHeaders(internalServerError.value(), payload.length);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(payload);
        }
    }

    private void respondNotFound(HttpExchange httpExchange, String fullPath) {
        try {
            String response = "Service not found by path: " + fullPath;
            byte[] payload = response.getBytes();
            httpExchange.sendResponseHeaders(HttpStatus.NOT_FOUND.value(), payload.length);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(payload);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] readFully(InputStream input) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;
        try(ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }
    }

}
