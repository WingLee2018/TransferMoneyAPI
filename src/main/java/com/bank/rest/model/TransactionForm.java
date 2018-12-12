/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bank.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class TransactionForm {
    
    @NotNull(message="Recipient account id must not be null or empty")
    @NotBlank(message="Recipient account id must not be null or empty")
    @JsonProperty("recipient_account_id")
    private String recipientAccountId;
    
    @NotNull(message="Transfer amount must not be null")
    @JsonProperty("transfer_amount")
    private Double transferAmount;

    public String getRecipientAccountId() {
        return recipientAccountId;
    }

    public void setRecipientAccountId(String recipientAccountId) {
        this.recipientAccountId = recipientAccountId;
    }

    public Double getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(Double transferAmount) {
        this.transferAmount = transferAmount;
    }
    
    
}
