/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bank.rest.exception;

public class AccountServiceException extends RuntimeException {

  private static final String ACCOUNT_SERVICE_ERROR = "Account service is not available";
  
  public AccountServiceException() {
    super(ACCOUNT_SERVICE_ERROR);
  }    
}