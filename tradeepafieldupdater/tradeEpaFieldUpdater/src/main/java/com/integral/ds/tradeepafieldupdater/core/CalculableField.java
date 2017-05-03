/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.integral.ds.tradeepafieldupdater.core;

/**
 *
 * @author Erick Oscategui
 */
public interface CalculableField {
    
   public void accept(CalculatorVisitor calculatorVisitor);
   
}
