/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bank.rest.exception;

public class AccountNotFoundException extends RuntimeException {

  private static final String ACCOUNT_NOT_FOUND_ERROR = "Account %s is not found";
  
  public AccountNotFoundException(String id) {
    super(String.format(ACCOUNT_NOT_FOUND_ERROR, id));
  }    
}
