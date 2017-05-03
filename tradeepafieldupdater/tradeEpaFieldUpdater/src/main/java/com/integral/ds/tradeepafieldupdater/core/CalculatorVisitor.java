/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.integral.ds.tradeepafieldupdater.core;

import com.integral.ds.tradeepafieldupdater.model.InternalTradeEpa;
import com.integral.ds.tradeepafieldupdater.model.TradeEpaField;

/**
 *
 * @author Erick Oscategui
 */
public interface CalculatorVisitor {
    
    public void visit(TradeEpaField field);
    
}
