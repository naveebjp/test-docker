/*
 * The MIT License
 *
 * Copyright 2015 Dmitry Noranovich.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.javaeeeee.dwstart;

import io.dropwizard.testing.junit.DropwizardAppRule;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.ClassRule;

import com.javaeeeee.dwstart.core.CalculateOrderRequest;
import com.javaeeeee.dwstart.core.CalculateOrderResponse;


/**
 * Integration Test for DWGettingStarted application.
 *
 * @author Dmitry Noranovich
 */
public class IntegrationTest {

    /**
     * Starts up the application.
     */
    @ClassRule
    public static final DropwizardAppRule<DWGettingStartedConfiguration> RULE
            = new DropwizardAppRule<>(DWGettingStartedApplication.class,
                    "config.yml");

    /**
     * Test secured greeting resource, happy path.
     */
    @Test
    public void testGetGreeting() {
        String expected = "Hello world!";
        //Obtain client
        Client client = ClientBuilder.newClient();
        //Build a feature in basic authentication mode
        HttpAuthenticationFeature feature
                = HttpAuthenticationFeature.basic("javaeeeee", "crimson");
        //Register the feature
        client.register(feature);
        //Get actual resul string
        String actual = client
                .target("http://localhost:8085/secured_hello")
                .request(MediaType.TEXT_PLAIN)
                .get(String.class);
        //Do an assertion
        assertEquals(expected, actual);

    }

    /**
     * Test secured greeting resource using HTTPS, happy path.
     */
    @Test
    public void testGetGreetingHttps() {
        String expected = "Hello world!";
        //Create SSL Configurator
        SslConfigurator sslConfigurator = SslConfigurator.newInstance();
        //Register a keystore
        sslConfigurator.trustStoreFile("dwstart.keystore")
                .trustStorePassword("crimson");
        //Create SSL Context
        SSLContext sSLContext = sslConfigurator.createSSLContext();
        //Obtain client
        Client client = ClientBuilder
                .newBuilder()
                .sslContext(sSLContext)
                .build();
        //Build a feature in basic authentication mode
        HttpAuthenticationFeature feature
                = HttpAuthenticationFeature.basic("javaeeeee", "crimson");
        //Register the feature
        client.register(feature);
        //Get actual resul string
        String actual = client
                .target("https://localhost:8443/secured_hello")
                .request(MediaType.TEXT_PLAIN)
                .get(String.class);
        //Do an assertion
        assertEquals(expected, actual);

    }

    /**
     * Test secured greeting resource, sad path.
     */
    @Test
    public void testGetGreetingUnauthorized() {
        //Obtain client
        Client client = ClientBuilder.newClient();
        //Build a feature in basic authentication mode with wrong credentials
        HttpAuthenticationFeature feature
                = HttpAuthenticationFeature.basic("user", "password");
        //Register the feature
        client.register(feature);
        //Get response
        Response response = client
                .target("http://localhost:8085/secured_hello")
                .request(MediaType.TEXT_PLAIN)
                .get();
        //Do an assertion
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(),
                response.getStatus());

    }
    
    
    @Test
    public void testCalculateOrder20()  {
    	
    	 String expected = "YUPROMO20";
    	 Client client = ClientBuilder.newClient();
    	 CalculateOrderRequest calculateOrderRequest = new CalculateOrderRequest();
    	 calculateOrderRequest.setOrderId("100");
    	 calculateOrderRequest.setQuantity(20);
    	//Get response
        CalculateOrderResponse response =  client
                .target("http://localhost:8085/hello")
                .request(MediaType.APPLICATION_JSON).post(Entity.entity(calculateOrderRequest,"application/json"), CalculateOrderResponse.class);
             
        String actual = response.getPromoCode();
        assertEquals(expected, actual);
       // System.out.println("expected 20  - YUPROMO20 :: " + actual);
    }
    
    @Test
    public void testCalculateOrder10()  {
    	
    	 String expected = "YUPROMO10";
    	 Client client = ClientBuilder.newClient();
    	 CalculateOrderRequest calculateOrderRequest = new CalculateOrderRequest();
    	 calculateOrderRequest.setOrderId("200");
    	 calculateOrderRequest.setQuantity(110);
    	//Get response
        CalculateOrderResponse response =  client
                .target("http://localhost:8085/hello")
                .request(MediaType.APPLICATION_JSON).post(Entity.entity(calculateOrderRequest,"application/json"), CalculateOrderResponse.class);
             
        String actual = response.getPromoCode();
        assertEquals(expected, actual);
       // System.out.println("expected 10  - YUPROMO10 :: " + actual);
    }
}
