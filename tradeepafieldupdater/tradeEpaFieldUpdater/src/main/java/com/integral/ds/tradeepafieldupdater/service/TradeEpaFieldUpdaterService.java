/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.integral.ds.tradeepafieldupdater.service;

import com.integral.ds.s3.S3RatesReader;
import com.integral.ds.tradeepa.dao.CassandraDao;
import com.integral.ds.tradeepa.dao.TradeRateDaoBase;
import com.integral.ds.tradeepafieldupdater.core.impl.FieldCalculatorVisitor;
import com.integral.ds.tradeepafieldupdater.dao.InternalTradeEpa022016Mapper;
import com.integral.ds.tradeepafieldupdater.dao.S3TradeRateDao;
import com.integral.ds.tradeepafieldupdater.model.TradeEpaField;
import com.integral.ds.tradeepafieldupdater.model.InternalTradeEpa022016;
import com.integral.ds.tradeepafieldupdater.model.TradeEpaConfig;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 *
 * @author Erick Oscategui
 */
@Service
public class TradeEpaFieldUpdaterService {
    
    private static String REJECTED_STATUS = "R";
    
    @Autowired
    private InternalTradeEpa022016Mapper tradeEpaMapper;
    
    private TradeRateDaoBase rateDao;
    
    private void init(TradeEpaConfig config, ApplicationContext context) {
        // If it's legacy we use the legacy dao, otherwise we use cassandra
        if ("legacy".equals(config.getMode())) {
            rateDao = (S3TradeRateDao) context.getBean("s3TradeRateDao");
        } else {
            rateDao = (CassandraDao) context.getBean("cassandraDao");
        }
    }
        
    public void updateFields(TradeEpaConfig config, ApplicationContext context) {
        init(config, context);
        System.out.println(" rateDao " + rateDao);
        if (rateDao instanceof S3TradeRateDao) {
            System.out.println(" Its an S3 dao ");
        } else {
            System.out.println(" Its a cassandra dao ");
        }
        List<InternalTradeEpa022016> tradeEpaList = tradeEpaMapper.selectByStatus(REJECTED_STATUS);
        try {
            //testS3RatesReaderSamplingTRPD(s3RatesDao);
            System.out.println(" Obtained tradeEpaList " + tradeEpaList);
            System.out.println(" Obtained tradeEpaList size " + tradeEpaList.size());

            for (InternalTradeEpa022016 tradeEpa : tradeEpaList) {
                System.out.println(" Processing trade " + tradeEpa.getTradeid());
                for (TradeEpaField field : config.getFieldList()) {
                    System.out.println(" Processing field " + field.getName());
                    FieldCalculatorVisitor visitor = new FieldCalculatorVisitor();
                    visitor.setTradeEpa(tradeEpa);
                    visitor.setRateDao(rateDao);
                    field.accept(visitor);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(TradeEpaFieldUpdaterService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
