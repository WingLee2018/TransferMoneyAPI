/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bank.rest.exception;

public class InsufficientFundException extends RuntimeException {

  private static final String INSUFFICIENT_FUND_ERROR = "Account has insufficient fund";
  
  public InsufficientFundException() {
    super(INSUFFICIENT_FUND_ERROR);
  }    
}
