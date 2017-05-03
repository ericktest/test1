/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.integral.ds.tradeepafieldupdater.util;

import java.math.BigDecimal;

/**
 *
 * @author hector.oscategui
 */
public class FieldUpdateUtil {
    
    /**
     * To check cwin fields
     * @param bid
     * @param ask
     * @param buysell
     * @param rate
     * @return 
     */
    public static Boolean checkBidAskWin(BigDecimal bid, BigDecimal ask, String buysell, BigDecimal rate) {
        Boolean win = false;
        if (buysell.trim().equals("B")) {
            if (ask.compareTo(rate) == 1) {
                win = true;
            }
        } else {
            if (bid.compareTo(rate) == -1) {
                win = true;
            }
        }
        return win;
    }
    
}
