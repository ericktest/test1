/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.integral.ds.tradeepafieldupdater.core.impl;

import com.integral.ds.tradeepa.dao.TradeRateDaoBase;
import com.integral.ds.tradeepafieldupdater.core.CalculationStrategy;
import com.integral.ds.tradeepafieldupdater.core.CalculatorVisitor;
import com.integral.ds.tradeepafieldupdater.model.InternalTradeEpa;
import com.integral.ds.tradeepafieldupdater.model.TradeEpaField;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Erick Oscategui
 */
public class FieldCalculatorVisitor implements CalculatorVisitor {

    private InternalTradeEpa tradeEpa;
    private TradeRateDaoBase rateDao;    
    
    @Override
    public void visit(TradeEpaField field) {
        String className = Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
        System.out.println(" Instantiating " + className);
        try {
            Class<?> c = Class.forName("com.integral.ds.tradeepafieldupdater.core.impl." + className + "Calculation");
            CalculationStrategy instance = (CalculationStrategy) c.newInstance();
            Object value = instance.doCalculation(tradeEpa, rateDao);
            System.out.println(" Value received " + value);
            field.setValue(value);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            Logger.getLogger(FieldCalculatorVisitor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            ex.printStackTrace();
            Logger.getLogger(FieldCalculatorVisitor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            Logger.getLogger(FieldCalculatorVisitor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the tradeEpa
     */
    public InternalTradeEpa getTradeEpa() {
        return tradeEpa;
    }

    /**
     * @param tradeEpa the tradeEpa to set
     */
    public void setTradeEpa(InternalTradeEpa tradeEpa) {
        this.tradeEpa = tradeEpa;
    }

    /**
     * @return the rateDao
     */
    public TradeRateDaoBase getRateDao() {
        return rateDao;
    }

    /**
     * @param rateDao the rateDao to set
     */
    public void setRateDao(TradeRateDaoBase rateDao) {
        this.rateDao = rateDao;
    }
    
}
