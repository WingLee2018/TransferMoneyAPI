/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bank.rest.api;

import com.bank.rest.exception.AccountNotFoundException;
import com.bank.rest.exception.AccountServiceException;
import com.bank.rest.exception.ErrorInfo;
import com.bank.rest.exception.InsufficientFundException;
import com.bank.rest.model.Balance;
import com.bank.rest.model.Transaction;
import com.bank.rest.model.TransactionForm;
import com.bank.utils.Utils;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class TransferMoneyController {

    private static final String BALANCE_URI = "http://localhost:8080/accounts/%s/balance";
    private static final int ID_LENGTH = 10;
    
    private static final List<String> ACCOUNT_IDS = Arrays.asList("user1", "user2", "user3");
    private static final List<Double> ACCOUNT_INITIAL_BALANCE = Arrays.asList(100000.0, 5000000.0, 20000.0);
    
    private static ConcurrentMap<String, Double> accountInfo = new ConcurrentHashMap<>();
    
    static {
        // Populate map with account initial balance
        for (int i = 0; i < ACCOUNT_IDS.size(); ++i) {
            accountInfo.putIfAbsent(ACCOUNT_IDS.get(i), ACCOUNT_INITIAL_BALANCE.get(i));
        }
    }
  
    @RequestMapping(
            value="/accounts/{id}/balance", 
            method=RequestMethod.GET, 
            produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
                            @ApiResponse(code = 400, message = "Bad Request", response = ErrorInfo.class ),
                            @ApiResponse(code = 404, message = "Not Found", response = ErrorInfo.class),
                            @ApiResponse(code = 500, message = "Internal server error occured", response = ErrorInfo.class) })   
    public ResponseEntity<Balance> getBalance(@PathVariable String id) {
     
        if (!isValidAccount(id))
            throw new AccountNotFoundException(id);
            
        Balance balance= new Balance(accountInfo.get(id));
        return new ResponseEntity<>(balance, HttpStatus.OK);
    }
    
    @RequestMapping(
            value="/accounts/{id}/transaction", 
            method=RequestMethod.POST, 
            produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Created"),
                            @ApiResponse(code = 400, message = "Bad Request", response = ErrorInfo.class ),
                            @ApiResponse(code = 404, message = "Not Found", response = ErrorInfo.class),
                            @ApiResponse(code = 500, message = "Internal server error occured", response = ErrorInfo.class) })   
    public ResponseEntity<Transaction> createTransaction(
            @PathVariable String id,
            @Valid @RequestBody TransactionForm form) {
     
        if (!isValidAccount(id))
            throw new AccountNotFoundException(id);

        if (!isValidAccount(form.getRecipientAccountId()))
            throw new AccountNotFoundException(form.getRecipientAccountId());
        
        // Compute sender new balance 
        double sender_new_balance = accountInfo.get(id) - form.getTransferAmount();
        
        // Return error if sender has insufficient fund
        if (sender_new_balance < 0)
            throw new InsufficientFundException();
                
        // Transfer the amount by updating the balance for sender and recipient
        accountInfo.put(id, sender_new_balance);
        accountInfo.put(form.getRecipientAccountId(), 
                accountInfo.get(form.getRecipientAccountId()) + form.getTransferAmount());
        
        Transaction response = new Transaction();
        Balance balance;
        
        // Calling API to retrieve the new balance of the sender
        try {
            RestTemplate restTemplate = new RestTemplate();
            String uri = String.format(BALANCE_URI, id);
            balance = restTemplate.getForObject(uri, Balance.class);
            if (balance == null)
                throw new AccountServiceException();
        } catch (Exception ex){
            throw new AccountServiceException();
        }
        
        response.setNew_balance(balance.getBalance());        
        response.setId(Utils.getRandomId(ID_LENGTH));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
       
    private boolean isValidAccount(final String id) {
        // Returns valid if id is not empty and one of pre-defined valid accounts
        return (id != null && ACCOUNT_IDS.contains(id));
    }
}
