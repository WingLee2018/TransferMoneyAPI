/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bank.utils;

import java.util.Random;

public class Utils {

    public static String getRandomId(int length) {
        String validChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        String generatedString = "";
        
        Random rand = new Random();
        
        for (int i = 0; i < length; i++)
            generatedString += validChars.charAt(rand.nextInt(validChars.length()));
        
        return generatedString;
    }    
}
