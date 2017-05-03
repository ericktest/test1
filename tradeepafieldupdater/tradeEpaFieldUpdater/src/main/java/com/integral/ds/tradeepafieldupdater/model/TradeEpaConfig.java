/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.integral.ds.tradeepafieldupdater.model;

import java.util.List;

/**
 *
 * @author Erick Oscategui
 */
public class TradeEpaConfig {

    // realtime | legacy
    private String mode; 

    // Fields to calculate
    private List<TradeEpaField> fieldList;

    /**
     * @return the mode
     */
    public String getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * @return the fieldList
     */
    public List<TradeEpaField> getFieldList() {
        return fieldList;
    }

    /**
     * @param fieldList the fieldList to set
     */
    public void setFieldList(List<TradeEpaField> fieldList) {
        this.fieldList = fieldList;
    }

}
