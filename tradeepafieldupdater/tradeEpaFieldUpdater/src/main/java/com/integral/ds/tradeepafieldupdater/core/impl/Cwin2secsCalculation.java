/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.integral.ds.tradeepafieldupdater.core.impl;

import com.integral.ds.emscope.rates.RatesObject;
import com.integral.ds.s3.S3RatesReader;
import com.integral.ds.tradeepa.dao.TradeRateDaoBase;
import com.integral.ds.tradeepafieldupdater.core.CalculationStrategy;
import com.integral.ds.tradeepafieldupdater.dao.S3TradeRateDao;
import com.integral.ds.tradeepafieldupdater.model.InternalTradeEpa;
import com.integral.ds.tradeepafieldupdater.model.InternalTradeEpa022016;
import com.integral.ds.tradeepafieldupdater.model.TradeEpaConfig;
import com.integral.ds.tradeepafieldupdater.model.TradeEpaField;
import com.integral.ds.tradeepafieldupdater.service.TradeEpaFieldUpdaterService;
import com.integral.ds.tradeepafieldupdater.temp.Log;
import com.integral.ds.tradeepafieldupdater.temp.PopulateCwin;
import com.integral.ds.tradeepafieldupdater.util.FieldUpdateUtil;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Erick Oscategui
 */
public class Cwin2secsCalculation implements CalculationStrategy {

    @Override
    public Object doCalculation(InternalTradeEpa tradeEpa, TradeRateDaoBase rateDao) {
        Boolean result = null;
        String ccyPair = null;
        Integer tradeRateTier = null;
        InternalTradeEpa022016 trade = (InternalTradeEpa022016) tradeEpa;
        System.out.println(" time " + trade.getTime());
        System.out.println(" time millis " + trade.getTime().getTime());
        System.out.println(" tradeRate " + trade.getTraderate());
        System.out.println(" stream " + trade.getStream());
        System.out.println(" org " + trade.getMakerorg());
        System.out.println(" ccyPair " + trade.getCcypair());
        System.out.println(" traderatetier " + trade.getTraderatetier());
        System.out.println(" buysell " + trade.getBuysell());
        Long fromTime = trade.getTime().getTime();
        Long endTime2secs = fromTime + 2000;
        ccyPair = trade.getCcypair().replace("/", "");
        List<RatesObject> rates = rateDao.getRates(trade.getMakerorg(), trade.getStream(), new Date(fromTime), new Date(endTime2secs), ccyPair);
        //System.out.println(" rates " + rates);
        System.out.println(" rates.size() " + rates.size());
        BigDecimal bidToCheck = null;
        BigDecimal askToCheck = null;
        // The rates are in time order, so we just need the final record with the correct tier
        tradeRateTier = trade.getTraderatetier();
        RatesObject r = null;
        for (RatesObject ro : rates){
            //System.out.println(" ro " + ro.getTmstmp() + " ro time " + new Timestamp(ro.getTmstmp()) + " ro bid " + ro.getBid_price() + " ro ask " + ro.getAsk_price() + " ro level " + ro.getLvl());
            if (tradeRateTier == ro.getLvl() && ccyPair.equals(ro.getCcyp())) {
                r = ro;
                bidToCheck = ro.getBid_price();
                askToCheck = ro.getAsk_price();
            }
        }
        System.out.println(" rate selected " + r + " with time " + new Timestamp(r.getTmstmp()));
        if (askToCheck != null) {
            result = FieldUpdateUtil.checkBidAskWin(bidToCheck, askToCheck, trade.getBuysell(), trade.getTraderate());
        }
        return result;
    }
    
    public static void main(String[] args) {
        String org = "CZBK";
        String stream = "FIXINTEG5";
        long fromTime = 1456336998204l;
        long toTime = 1456337002218l;
        String ccyPair = "EURUSD";
        TradeRateDaoBase rateDao = new S3TradeRateDao();
        List<RatesObject> rates = rateDao.getRates(org, stream, new Date(fromTime), new Date(toTime), ccyPair);
        System.out.println(" rates " + rates.size());
        System.out.println(" rates " + rates);
    }
    
}
