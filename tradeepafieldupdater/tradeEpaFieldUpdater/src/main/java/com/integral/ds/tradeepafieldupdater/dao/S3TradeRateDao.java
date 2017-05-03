/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.integral.ds.tradeepafieldupdater.dao;

import com.integral.ds.db.BenchmarkModel;
import com.integral.ds.emscope.rates.RatesObject;
import com.integral.ds.s3.S3RatesReader;
import com.integral.ds.tradeepa.dao.TradeRateDaoBase;
import com.integral.ds.tradeepa.domain.PrvdrStreamQuoteCollectionV2;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 *
 * @author Erick Oscategui
 */
@Component
public class S3TradeRateDao implements TradeRateDaoBase {
    
    S3RatesReader s3RatesDao;

    public S3TradeRateDao() {
        this.s3RatesDao = new S3RatesReader();
    }

    @Override
    public Map<String, Map<Long, BenchmarkModel>> getBenchmarkRates(String ccypair, Long fromTime, Long endTime, String[] args) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PrvdrStreamQuoteCollectionV2 getQuoteBook(String provider, String stream, String ccypair, Long fromTime, Long endTime, String[] args) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public List<RatesObject> getRates(String provider, String stream, Date fromTime, Date endTime, String ccyPair) {
        return s3RatesDao.getRates(provider, stream, fromTime, endTime, ccyPair);
    }
    
}
