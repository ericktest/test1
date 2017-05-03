/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.integral.ds.tradeepafieldupdater.core;

import com.integral.ds.tradeepa.dao.TradeRateDaoBase;
import com.integral.ds.tradeepafieldupdater.model.InternalTradeEpa;

/**
 *
 * @author Erick Oscategui
 */
public interface CalculationStrategy {
    
   public Object doCalculation(InternalTradeEpa tradeEpa, TradeRateDaoBase rateDao);
   
}
