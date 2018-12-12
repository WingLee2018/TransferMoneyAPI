/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bank.rest.api;

import com.bank.rest.model.TransactionForm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@WebMvcTest(TransferMoneyController.class)
public class TransferMoneyControllerTest extends TestCase {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Test of getBalance method, of class TransferMoneyController.
     */
    @Test
    public void testGetBalance() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/accounts/user1/balance");
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(100000));
    }

    @Test
    public void testGetBalance_invalidUser() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/accounts/abc/balance");
        mockMvc.perform(builder)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error_code").value(404))
                .andExpect(jsonPath("$.error_description").value("Account abc is not found"));
    }
    
    /**
     * Test of createTransaction method, of class TransferMoneyController.
     */
    @Test
    public void testCreateTransaction() throws Exception {
        HttpServer httpServer = null;
        try {
            // Setup a mock web server to retrieve balance
            httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
            httpServer.createContext("/accounts/user1/balance", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    byte[] response = "{\"balance\":70000}".getBytes();
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length);
                    exchange.getResponseBody().write(response);
                    exchange.close();
                }
            });
            httpServer.start();

            TransactionForm transaction = new TransactionForm();
            transaction.setRecipientAccountId("user2");
            transaction.setTransferAmount(30000.0);
            MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/accounts/user1/transaction")
                    .content(convertToJsonString(transaction))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);
            mockMvc.perform(builder)
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.new_balance").value(70000.0));
               
        } finally {
            httpServer.stop(0);
        }
        
        // Verify recipient balance
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/accounts/user2/balance");
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(5030000));
    }

    @Test
    public void testCreateTransaction_invalidSender() throws Exception {
        TransactionForm transaction = new TransactionForm();
        transaction.setRecipientAccountId("user2");
        transaction.setTransferAmount(30000.0);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/accounts/dummy/transaction")
                .content(convertToJsonString(transaction))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(builder)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error_code").value(404))
                .andExpect(jsonPath("$.error_description").value("Account dummy is not found"));
    }

    @Test
    public void testCreateTransaction_emptyRecipient() throws Exception {
        TransactionForm transaction = new TransactionForm();
        transaction.setTransferAmount(30000.0);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/accounts/user1/transaction")
                .content(convertToJsonString(transaction))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_code").value(400))
                .andExpect(jsonPath("$.error_description").value("Recipient account id must not be null or empty"));
    }

    @Test
    public void testCreateTransaction_emptyTransferAmount() throws Exception {
        TransactionForm transaction = new TransactionForm();
        transaction.setRecipientAccountId("user2");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/accounts/user1/transaction")
                .content(convertToJsonString(transaction))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_code").value(400))
                .andExpect(jsonPath("$.error_description").value("Transfer amount must not be null"));
    }
    
    @Test
    public void testCreateTransaction_invalidRecipient() throws Exception {
        TransactionForm transaction = new TransactionForm();
        transaction.setRecipientAccountId("dummy");
        transaction.setTransferAmount(30000.0);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/accounts/user1/transaction")
                .content(convertToJsonString(transaction))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(builder)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error_code").value(404))
                .andExpect(jsonPath("$.error_description").value("Account dummy is not found"));
    }

    public void testCreateTransaction_insufficientFund() throws Exception {
        TransactionForm transaction = new TransactionForm();
        transaction.setRecipientAccountId("user2");
        transaction.setTransferAmount(300000.0);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/accounts/user1/transaction")
                .content(convertToJsonString(transaction))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_code").value(400))
                .andExpect(jsonPath("$.error_description").value("Account has insufficient fund"));
    }
    
    @Test
    public void testCreateTransaction_accountServiceDown() throws Exception {
        TransactionForm transaction = new TransactionForm();
        transaction.setRecipientAccountId("user2");
        transaction.setTransferAmount(30000.0);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/accounts/user1/transaction")
                .content(convertToJsonString(transaction))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(builder)
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error_code").value(500))
                .andExpect(jsonPath("$.error_description").value("Account service is not available"));
    }
    
    private String convertToJsonString(final TransactionForm transaction) throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(transaction);
    }
}
