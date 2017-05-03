/*
 * Decompiled with CFR 0_103.
 */
package com.integral.ds.tradeepafieldupdater.temp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataService {

    static Connection monetConn;
    static String ip = "localhost";
    static String user = "root";
    static String port = "3306";
    static String pwd = null;
//    public static String ip = "10.0.0.171";
//    public static String user = "rep1";    
//    public static String pwd = "integral";
//    public static String port ="3310";

    public static Connection getMySqlConnection() {
        System.out.println("Connecting MySql....");
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/grdmon", user, pwd);
            System.out.println("Done");
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
        return conn;
    }

    public static Connection getPostgresConnection() {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://eastot.chgyeswrwlad.us-east-1.rds.amazonaws.com:5432/eastOT", "emscope", "Integral4309");
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
        return conn;
    }

    public static Connection getMonetDbConnection() {
        String ip = "localhost";
        try {
            System.out.println("Connecting Monet....");
            Class.forName("nl.cwi.monetdb.jdbc.MonetDriver");
            monetConn = DriverManager.getConnection("jdbc:monetdb://" + ip + ":50000/epa", "monetdb", "monetdb");
            System.out.println("Done");
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
        return monetConn;
    }
}
