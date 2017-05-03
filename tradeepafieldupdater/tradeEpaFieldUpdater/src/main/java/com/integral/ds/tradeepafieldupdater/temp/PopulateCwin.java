/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.integral.ds.tradeepafieldupdater.temp;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.integral.ds.emscope.rates.RatesObject;
import com.integral.ds.tradeepafieldupdater.temp.S3RatesReader2;
import com.integral.ds.util.DataQueryUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipFile;
import org.joda.time.DateTime;

/**
 *
 * @author johngilman
 */
public class PopulateCwin {

    Connection conn;
    PreparedStatement prep, upPrep;
    private static final DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    private static final DateFormat df2 = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss.SSS", Locale.ENGLISH);
    private static final DateFormat df3 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private static final DateFormat df4 = new SimpleDateFormat("MM_yyyy", Locale.ENGLISH);
    private static final DateFormat df5 = new SimpleDateFormat("yyyy-MM-dd-HH", Locale.ENGLISH);
    int recCnt = 0;
    List<String> minuteList = new ArrayList();
    String currTradeId = "";
    DateTime execSave = new DateTime();
    S3RatesReader2 ratesReader = new S3RatesReader2();
    Map<String, ZipFile> zipMap;
    String sqlStr = "";
    String currRow = "";

    public PopulateCwin(String start, String end, String fileStr) {
        this.zipMap = new HashMap();
        try {
            conn = DataService.getPostgresConnection();
            sqlStr = "SELECT t.tradeid, t.maker_org, t.stream, t.exectime , "
                    + " t.rate, t.ccypair, t.buysell,it.traderatetier "
                    //+ " from  trades_" + fileStr + " t, internal_trade_epa_" + fileStr + " it "
                    + " from  trades_02_2016 t, internal_trade_epa_02_2016 it "
                    + " where t.status='R' "
                    + " and t.tradeid =it.tradeid "
                    + " and it.ratesrelatederrorcode='NONE' "
                    //                    + " and it.cwin2secs is  null "
                    //+ " and t.exectime between '" + start + "' and '" + end + "'"
                    + " and it.tradeid = 'FXI4887022384' "
                    + "  order by maker_org, stream, ccypair, exectime";

            prep = conn.prepareStatement(sqlStr);
            upPrep = conn.prepareStatement("UPDATE  internal_trade_epa_" + fileStr
                    + " set cwin2secs=?, cwin4secs=? "
                    + " where tradeid =?");

        } catch (SQLException ex) {
            Log.error(ex.getMessage());
        }
    }

    public void getTradeEpaRecs() {
        try {

            ResultSet rs = prep.executeQuery();
            List<RatesObject> rateObjs = new ArrayList();
            String hourStr = "";
            System.out.println("done with query");
//            Log.info(sqlStr);
            while (rs.next()) {
                Map<String, Boolean> winMap = new HashMap();
                String tradeid = rs.getString("tradeid");
                Timestamp execTime = rs.getTimestamp("exectime");
                String makerOrg = rs.getString("maker_org");
                String stream = rs.getString("stream");
                BigDecimal rate = new BigDecimal(rs.getDouble("rate"));
                String ccypair = rs.getString("ccypair");
                String buySell = rs.getString("buysell");
                Integer tradeRateTier = rs.getInt("traderatetier");
                Log.info("execTime: " + df1.format(execTime) + "    " + tradeid);
                currTradeId = tradeid;
//                if (tradeid.equals("FXI4064660662")) {
//                    Log.info(tradeid);
//                }
                System.out.println(" curr time " + execTime);
                System.out.println(" curr time millis " + execTime.getTime());
                System.out.println(" curr tradeRate " + rate);
                System.out.println(" curr stream " + stream);
                System.out.println(" curr org " + makerOrg);
                System.out.println(" curr ccyPair " + ccypair);
                System.out.println(" curr traderatetier " + tradeRateTier);
                System.out.println(" curr buysell " + buySell);
                updateWins(tradeid, makerOrg, stream, ccypair, execTime, buySell, rate, tradeRateTier);
            }
        } catch (SQLException ex) {
            Log.error(ex.getMessage());
        }
    }

    public void updateWins(String tradeId, String makerOrg, String stream, String ccypair, Timestamp execTime,
            String buySell, BigDecimal rate, Integer tradeRateTier) throws SQLException {
        try {
            Map<String, Boolean> winMap = new HashMap();
            DateTime exec = new DateTime(execTime);
            String newRow = makerOrg + "|" + stream + "|" + ccypair + "|" + exec.getDayOfWeek() + "|" + exec.getHourOfDay();

            if (!currRow.equals(newRow)) {
                winMap = getTradeEpaRates(makerOrg, stream, ccypair.replace("/", ""),
                        execTime, tradeRateTier, buySell, rate, true);
                currRow = newRow;
            } else {
                // don't create new zip, use same hourly
                winMap = getTradeEpaRates(makerOrg, stream, ccypair.replace("/", ""),
                        execTime, tradeRateTier, buySell, rate, false);
            }
            execSave = exec;
            if (winMap == null || winMap.size() < 1) {
                Log.info("no data in winMap");
                return;
            }
            Boolean win2 = null;
            Boolean win4 = null;
            System.out.println("winmap: " + winMap);
            win2 = winMap.get("twoSec");
            win4 = winMap.get("fourSec");
            if (win2 == null) {
                upPrep.setNull(1, Types.BOOLEAN);
            } else {
                upPrep.setBoolean(1, winMap.get("twoSec"));
            }
            if (win4 == null) {
                upPrep.setNull(2, Types.BOOLEAN);
            } else {
                upPrep.setBoolean(2, winMap.get("fourSec"));
            }
            upPrep.setString(3, tradeId);
            //upPrep.executeUpdate();
            recCnt++;
//                    if (recCnt % 100 == 0){
//                        upPrep.executeBatch();
            System.out.println("count: " + recCnt + "    " + currRow);

        } catch (Exception e) {
            Log.error(e.getMessage());
        }

    }

    public Boolean checkBidAskWin(Map<String, BigDecimal> bidAskMap,
            String buysell, BigDecimal rate) {
        Boolean win = false;
        if (buysell.trim().equals("B")) {
            if (bidAskMap.get("ask").compareTo(rate) == 1) {
                win = true;
            }
        } else {
            if (bidAskMap.get("bid").compareTo(rate) == -1) {
                win = true;
            }
        }
        return win;
    }

    private Map<String, BigDecimal> findBidAsk(DateTime timeIn, String hourStr,
            String ccypair, Integer tradeRateTier) {
        Map<String, BigDecimal> bidAsk = new HashMap();
        try {
            Boolean found = false;
            String ccypair2 = ccypair.replace("/", "");
            String dateStr = df2.format(timeIn.toDate());
//        String[] tradeRates = hourStr.split("\\n");
            String[] rateFlds = null;
            BufferedReader inbuff = new BufferedReader(new StringReader(hourStr));
            String rateLine = "";
            String bidStr = "";
            String offerStr = "";
            String recTimeStr = "";
            String prevBidStr = "";
            String prevOfferStr = "";
            String prevRecTimeStr = "";
            String lineSelected = "";
            String prevLineSelected = "";
            while ((rateLine = inbuff.readLine()) != null) {
                rateFlds = rateLine.split(",");
                if (rateFlds == null || rateFlds.length < 9) {
                    continue;
                }
                DateTime recTime = new DateTime();
                try {
                    recTime = new DateTime(df2.parse(rateFlds[3]));
//                Log.info(rateFlds[3]);
                } catch (ParseException ex) {
                    continue;
                }
                lineSelected = rateLine;
                bidStr = rateFlds[6];
                offerStr = rateFlds[8];
                recTimeStr = rateFlds[3];
                //Log.info("time " + rateFlds[3] + " tier " + rateFlds[4]);
                if (ccypair2.equals(rateFlds[2]) && recTime.isAfter(timeIn)
                        && rateFlds[4].equals(String.valueOf(tradeRateTier))) {
                    found = true;
                    break;
                }
                if (rateFlds[4].equals(String.valueOf(tradeRateTier))) {
                prevBidStr = bidStr;
                prevOfferStr = offerStr;
                prevRecTimeStr = recTimeStr;
                prevLineSelected = lineSelected;
                }
            }
            /*if (found) {
                bidAsk.put("bid", new BigDecimal(bidStr));
                bidAsk.put("ask", new BigDecimal(offerStr));
                Log.info("recTimeStr" + recTimeStr);
                Log.info("bid: " + bidStr);
                Log.info("ask: " + rateFlds[8]);
            }*/
            if (found) {
                bidAsk.put("bid", new BigDecimal(prevBidStr));
                bidAsk.put("ask", new BigDecimal(prevOfferStr));
                Log.info("rate selected " + prevLineSelected);
                Log.info("recTimeStr" + prevRecTimeStr);
                Log.info("bid: " + prevBidStr);
                Log.info("ask: " + prevOfferStr);
            } else {
                Log.info("null bid/ask");
                bidAsk.put("bid", null);
                bidAsk.put("ask", null);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bidAsk;
    }

    
    public Map<String, Boolean> getTradeEpaRates(String provider, String stream,
            String ccypair, Timestamp execTime, Integer tradeRateTier,
            String buySell, BigDecimal rate, boolean newZip) {
        long startClock = System.currentTimeMillis();
        DateTime twoSecs = new DateTime(execTime).plusSeconds(2);
        Log.info("curr twoSecs: " + twoSecs);
        Log.info("curr twoSecs millis: " + twoSecs.getMillis());
        DateTime fourSecs = new DateTime(execTime).plusSeconds(4);
        Log.info("curr fourSecs: " + fourSecs);
        Log.info("curr fourSecs millis: " + fourSecs.getMillis());
        Map<String, Boolean> winMap = new HashMap();
        try {
            if (newZip) {
                closeZips();
                getZipFileMap(twoSecs, fourSecs, provider, stream, ccypair);
            }
            Log.info("currTradeId: " + currTradeId);
            String zipFileName = df5.format(twoSecs.toDate()) + ".zip";
            ZipFile zip = zipMap.get(zipFileName);
            Log.info("zipMap.get(zipFileName): " + zipMap.get(zipFileName));
            Log.info("zipFileName: " + zipFileName);
            String twoSecMinStr = ratesReader.getMinuteClob(twoSecs, zip);
            //Log.info("twoSecMinStr: " + twoSecMinStr);
            zipFileName = df5.format(fourSecs.toDate()) + ".zip";
            Log.info("zipFileName: " + zipFileName);
            String fourSecMinStr = ratesReader.getMinuteClob(fourSecs, zipMap.get(zipFileName));
            //Log.info("fourSecMinStr: " + fourSecMinStr);
            Map<String, BigDecimal> twoSecBidAskMap = findBidAsk(twoSecs, twoSecMinStr, ccypair, tradeRateTier);
            Map<String, BigDecimal> fourSecBidAskMap = findBidAsk(fourSecs, fourSecMinStr, ccypair, tradeRateTier);
            if (twoSecBidAskMap != null && twoSecBidAskMap.get("ask") != null) {
                Boolean twoWin = checkBidAskWin(twoSecBidAskMap, buySell, rate);
                winMap.put("twoSec", twoWin);
            }
            if (fourSecBidAskMap != null && fourSecBidAskMap.get("ask") != null) {
                Boolean fourWin = checkBidAskWin(fourSecBidAskMap, buySell, rate);
                winMap.put("fourSec", fourWin);
            }

        } catch (AmazonS3Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Time taken " + (System.currentTimeMillis() - startClock));

        return winMap;
    }

    public void closeZips() {
        try {
            Log.info("closing zips");
            Set<String> zipSet = zipMap.keySet();
            for (String zipName : zipSet) {
                ZipFile zip = zipMap.get(zipName);
                zip.close();
                File file = new File(zipName);
                file.delete();
            }
            zipMap.clear();
        } catch (IOException ex) {
            Log.error(ex.getMessage());
        }
    }

    private void getZipFileMap(DateTime twoSecs, DateTime fourSecs, String provider, String stream, String ccypair) {
        List<DataQueryUtils.S3FileInfo> listOfS3Files
                = DataQueryUtils.getS3FileListForLongerDuration(twoSecs.toDate(), fourSecs.toDate(), provider, stream, ccypair);
        int fileCnt = 0;
        for (DataQueryUtils.S3FileInfo fileInfo : listOfS3Files) {
            System.out.println(" fileInfo " + fileInfo);
            String zipFileName = df5.format(twoSecs.toDate()) + ".zip";
            if (fileCnt > 0) {
                zipFileName = df5.format(fourSecs.toDate()) + ".zip";
            }
            ratesReader.downloadHourFile(fileInfo.getFilePath(), zipFileName);
            ZipFile zip = ratesReader.getZipFile(zipFileName);
            zipMap.put(zipFileName, zip);
            fileCnt++;
        }
    }

    public static void main(String[] args) {
        try {
            Date start = df3.parse(args[0]);
            String fileStr = df4.format(start);
            PopulateCwin cwin = new PopulateCwin(args[0], args[1], fileStr);
            cwin.getTradeEpaRecs();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
