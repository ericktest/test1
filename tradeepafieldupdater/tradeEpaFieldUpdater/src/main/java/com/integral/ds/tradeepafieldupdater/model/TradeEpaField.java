/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.integral.ds.tradeepafieldupdater.model;

import com.integral.ds.tradeepafieldupdater.core.CalculableField;
import com.integral.ds.tradeepafieldupdater.core.CalculatorVisitor;

/**
 *
 * @author Erick Oscaetgui
 */
public class TradeEpaField implements CalculableField {
    
    private String name;
    private String type;
    private Object value;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public void accept(CalculatorVisitor calculatorVisitor) {
        calculatorVisitor.visit(this);
    }
    
}
