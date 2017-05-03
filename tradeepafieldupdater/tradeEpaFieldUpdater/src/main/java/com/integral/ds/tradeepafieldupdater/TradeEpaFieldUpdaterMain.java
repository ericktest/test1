/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.integral.ds.tradeepafieldupdater;

import com.integral.ds.tradeepafieldupdater.model.TradeEpaConfig;
import com.integral.ds.tradeepafieldupdater.model.TradeEpaField;
import com.integral.ds.tradeepafieldupdater.service.TradeEpaFieldUpdaterService;
import com.integral.ds.tradeepafieldupdater.temp.PopulateCwin;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Erick Oscategui
 */
public class TradeEpaFieldUpdaterMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-module.xml");
        TradeEpaConfig config = new TradeEpaConfig();
        config.setMode("legacy");
        List<TradeEpaField> fieldList = new ArrayList<TradeEpaField>();
        TradeEpaField field = new TradeEpaField();
        field.setName("cwin2secs");
        field.setType("boolean");
        fieldList.add(field);
        field = new TradeEpaField();
        field.setName("cwin4secs");
        field.setType("boolean");
        fieldList.add(field);
        config.setFieldList(fieldList);
        TradeEpaFieldUpdaterService service = (TradeEpaFieldUpdaterService) context.getBean("tradeEpaFieldUpdaterService");
        System.out.println(" Begin processing with following field list " + fieldList);
        service.updateFields(config, context);
        PopulateCwin cwin = new PopulateCwin(null, null, null);
        cwin.getTradeEpaRecs();        
    }
    
}
