package com.account.service.http;

import com.account.service.config.Configuration;
import com.account.service.dto.AccountDTO;
import com.account.service.dto.TransferDTO;
import com.account.service.exceptions.AccountNotFoundException;
import com.account.service.exceptions.InsufficientMoneyException;
import com.account.service.exceptions.InternalRepositoryException;
import com.account.service.model.Account;
import com.account.service.service.AccountService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.account.service.config.HttpServerConfiguration.CONTEXT_ROOT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class HttpAccountServiceTest {

    private static final String BASE_URL = "http://localhost:8080" + CONTEXT_ROOT + "/";

    private static HttpAccountService service;
    private static AccountService accountService;

    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);

    private final TransferDTO transferDTO = new TransferDTO(1, 1, new BigDecimal(50.00).setScale(2, RoundingMode.DOWN));

    @BeforeClass
    public static void setUp() throws Exception {
        accountService = mock(AccountService.class);
        AccountDTO accountDTO1 = new AccountDTO(new Account(1, 1, new BigDecimal(50.00)));
        AccountDTO accountDTO2 = new AccountDTO(new Account(2, 2, new BigDecimal(150.00)));
        when(accountService.findById(1)).thenReturn(accountDTO1);
        when(accountService.findById(2)).thenReturn(accountDTO2);
        when(accountService.findById(3)).thenThrow(AccountNotFoundException.class);
        when(accountService.findById(4)).thenThrow(InternalRepositoryException.class);
        service = new HttpAccountService(new Configuration(), accountService);
        service.initialize();
        service.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        service.stop();
    }

    @Test
    public void testFindById() throws Exception {
        HttpUriRequest request = new HttpGet( BASE_URL + "findById/1");

        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        assertEquals(httpResponse.getStatusLine().getStatusCode(), HttpStatus.OK.value());

        AccountDTO accountDTO = retrieveResourceFromResponse(httpResponse, AccountDTO.class);
        assertEquals(1, accountDTO.getId());
        assertEquals(1, accountDTO.getUserId());
        assertEquals(new BigDecimal(50.00).setScale(2, RoundingMode.DOWN), accountDTO.getAmount());
}

    @Test
    public void testFindByIdBadHttpMethod() throws Exception {
        HttpUriRequest request = new HttpPost( BASE_URL + "findById/1");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        assertEquals(HttpStatus.BAD_REQUEST.value(), httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void testFindByIdAccountNotFound() throws Exception {
        HttpUriRequest request = new HttpGet(BASE_URL + "findById/3");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        assertEquals(HttpStatus.ACCOUNT_NOT_FOUND.value(), httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void testFindByIdBadParameter() throws Exception {
        HttpUriRequest request = new HttpGet( BASE_URL + "findById/asdf");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        assertEquals(HttpStatus.BAD_REQUEST.value(), httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void testFindByIdInternalError() throws Exception {
        HttpUriRequest request = new HttpGet( BASE_URL + "findById/4");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        assertEquals( HttpStatus.INTERNAL_SERVER_ERROR.value(), httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void testTransfer() throws Exception {
        HttpPost request = new HttpPost( BASE_URL + "transfer");
        request.setEntity(new ByteArrayEntity(mapper.writeValueAsBytes(transferDTO)));
        doNothing().when(accountService).transfer(any());
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        assertEquals(HttpStatus.OK.value(), httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void testTransferBadHttpMethod() throws Exception {
        HttpGet request = new HttpGet( BASE_URL + "transfer");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        assertEquals(HttpStatus.BAD_REQUEST.value(), httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void testTransferAccountNotFoundException() throws Exception {
        HttpPost request = new HttpPost( BASE_URL + "transfer");
        request.setEntity(new ByteArrayEntity(mapper.writeValueAsBytes(transferDTO)));
        doThrow(new AccountNotFoundException()).when(accountService).transfer(any());
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        assertEquals(HttpStatus.ACCOUNT_NOT_FOUND.value(), httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void testTransferInsufficientMoneyException() throws Exception {
        HttpPost request = new HttpPost(BASE_URL+ "transfer");
        request.setEntity(new ByteArrayEntity(mapper.writeValueAsBytes(transferDTO)));
        doThrow(new InsufficientMoneyException()).when(accountService).transfer(any());
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        assertEquals(HttpStatus.INSUFFICIENT_MONEY.value(), httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void testTransferInternalRepositoryException() throws Exception {
        HttpPost request = new HttpPost( BASE_URL + "transfer");
        request.setEntity(new ByteArrayEntity(mapper.writeValueAsBytes(transferDTO)));
        doThrow(new InternalRepositoryException()).when(accountService).transfer(any());
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void testTransferBadRequest() throws Exception {
        HttpPost request = new HttpPost( BASE_URL + "transfer");
        request.setEntity(new ByteArrayEntity(mapper.writeValueAsBytes(transferDTO)));
        doThrow(new RuntimeException()).when(accountService).transfer(any());
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), httpResponse.getStatusLine().getStatusCode());
    }

    private <T> T retrieveResourceFromResponse(HttpResponse response, Class<T> clazz) throws IOException {
        String jsonFromResponse = EntityUtils.toString(response.getEntity());
        return mapper.readValue(jsonFromResponse, clazz);
    }

}