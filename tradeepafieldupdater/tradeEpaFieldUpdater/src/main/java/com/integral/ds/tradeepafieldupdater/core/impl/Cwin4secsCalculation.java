/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.integral.ds.tradeepafieldupdater.core.impl;

import com.integral.ds.emscope.rates.RatesObject;
import com.integral.ds.tradeepa.dao.TradeRateDaoBase;
import com.integral.ds.tradeepafieldupdater.core.CalculationStrategy;
import com.integral.ds.tradeepafieldupdater.model.InternalTradeEpa;
import com.integral.ds.tradeepafieldupdater.model.InternalTradeEpa022016;
import com.integral.ds.tradeepafieldupdater.util.FieldUpdateUtil;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Erick Oscategui
 */
public class Cwin4secsCalculation implements CalculationStrategy{

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
        Long fromTime = trade.getTime().getTime();
        Long endTime4secs = fromTime + 4000;
        ccyPair = trade.getCcypair().replace("/", "");
        List<RatesObject> rates = rateDao.getRates(trade.getMakerorg(), trade.getStream(), new Date(fromTime), new Date(endTime4secs), ccyPair);
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
    
}
