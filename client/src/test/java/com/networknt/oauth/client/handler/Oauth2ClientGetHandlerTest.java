package com.networknt.oauth.client.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.networknt.client.Http2Client;
import com.networknt.config.Config;
import com.networknt.exception.ApiException;
import com.networknt.exception.ClientException;
import com.networknt.status.Status;
import io.undertow.UndertowOptions;
import io.undertow.client.ClientConnection;
import io.undertow.client.ClientRequest;
import io.undertow.client.ClientResponse;
import io.undertow.util.Methods;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.IoUtils;
import org.xnio.OptionMap;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
* Generated by swagger-codegen
*/
public class Oauth2ClientGetHandlerTest {
    @ClassRule
    public static TestServer server = TestServer.getInstance();

    static final Logger logger = LoggerFactory.getLogger(Oauth2ClientGetHandlerTest.class);

    @Test
    public void testOauth2ClientGetHandler() throws ClientException, ApiException {
        final Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI("https://localhost:6884"), Http2Client.WORKER, Http2Client.SSL, Http2Client.POOL, OptionMap.create(UndertowOptions.ENABLE_HTTP2, true)).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        try {
            ClientRequest request = new ClientRequest().setMethod(Methods.GET).setPath("/oauth2/client?page=1");
            connection.sendRequest(request, client.createClientCallback(reference, latch));
            latch.await();
            int statusCode = reference.get().getResponseCode();
            String body = reference.get().getAttachment(Http2Client.RESPONSE_BODY);
            logger.debug("statusCode = " + statusCode);
            logger.debug("body = " + body);
            Assert.assertEquals(200, statusCode);
            if(statusCode == 200) {
                // make sure that there are two services in the result.
                List<com.networknt.oauth.cache.model.Client> clients = Config.getInstance().getMapper().readValue(body, new TypeReference<List<com.networknt.oauth.cache.model.Client>>(){});
                Assert.assertTrue(clients.size() >= 1 && clients.size() <= 10);
                // make sure that the first is AACT0001
                // Assert.assertEquals("PetStore Web Server", clients.get(0).getClientName());
                // make sure that client_secret is null.
                Assert.assertNull(clients.get(0).getClientSecret());
            }
        } catch (Exception e) {
            logger.error("Exception: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
    }

    @Test
    public void testPageMissing() throws ClientException, ApiException {
        final Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI("https://localhost:6884"), Http2Client.WORKER, Http2Client.SSL, Http2Client.POOL, OptionMap.create(UndertowOptions.ENABLE_HTTP2, true)).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        try {
            ClientRequest request = new ClientRequest().setMethod(Methods.GET).setPath("/oauth2/client");
            connection.sendRequest(request, client.createClientCallback(reference, latch));
            latch.await();
            int statusCode = reference.get().getResponseCode();
            String body = reference.get().getAttachment(Http2Client.RESPONSE_BODY);
            Assert.assertEquals(400, statusCode);
            if(statusCode == 400) {
                Status status = Config.getInstance().getMapper().readValue(body, Status.class);
                Assert.assertNotNull(status);
                Assert.assertEquals("ERR11000", status.getCode());
                Assert.assertEquals("VALIDATOR_REQUEST_PARAMETER_QUERY_MISSING", status.getMessage()); // page is missing
            }
        } catch (Exception e) {
            logger.error("Exception: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
    }
}
