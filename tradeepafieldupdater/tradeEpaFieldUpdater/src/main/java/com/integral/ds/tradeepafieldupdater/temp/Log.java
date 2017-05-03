/*
 * Decompiled with CFR 0_103.
 */
package com.integral.ds.tradeepafieldupdater.temp;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Log {
    private static final Logger logger = Logger.getGlobal();
    private static ConsoleHandler cHandler;
    public static Level logLevel;
    private static final String classname;

    private static String callerRef() {
        int i;
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        if (stackTraceElements.length < 4) {
            return "";
        }
        for (i = 1; i < stackTraceElements.length && !stackTraceElements[i].getClassName().equals(classname); ++i) {
        }
        while (i < stackTraceElements.length && stackTraceElements[i].getClassName().equals(classname)) {
            ++i;
        }
        if (i < stackTraceElements.length) {
            return stackTraceElements[i].toString();
        }
        return "[in unknown method]";
    }

    public static void setLogLevel(Level newLogLevel) {
        logLevel = newLogLevel;
        cHandler.setLevel(newLogLevel);
    }

    public static int getLevelNum() {
        return logLevel.intValue();
    }

    public static int getLevelNum(Level level) {
        return level.intValue();
    }

    public static void fine(String msg) {
        logger.log(Level.FINE, msg + "\t " + Log.callerRef());
    }

    public static void info(String msg) {
        logger.log(Level.INFO, msg + "\t " + Log.callerRef());
    }

    public static void warning(String msg) {
        logger.log(Level.WARNING, msg + "\t " + Log.callerRef());
    }

    public static void error(String msg) {
        logger.log(Level.SEVERE, msg + "\t " + Log.callerRef());
    }

    public static void exception(String msg, Throwable cause) {
        logger.log(Level.SEVERE, msg + "\t " + Log.callerRef(), cause);
    }

    static {
        logLevel = Level.INFO;
        try {
            Handler[] rootHandlers;
            Logger rootLogger = Logger.getLogger("");
            for (Handler handler : rootHandlers = rootLogger.getHandlers()) {
                rootLogger.removeHandler(handler);
            }
            cHandler = new ConsoleHandler();
            cHandler.setFormatter(new LogFormatter());
            cHandler.setLevel(logLevel);
            logger.addHandler(cHandler);
            
            FileHandler fHandler = new FileHandler("ordertrade%g.log", 100000, 2);
            fHandler.setLevel(Level.INFO);
            fHandler.setFormatter(new LogFormatter());
            logger.addHandler(fHandler);
            logger.setLevel(Level.INFO);
        }
        catch (IOException | SecurityException ex) {
            ex.printStackTrace();
        }
        classname = Log.class.getName();
    }

    public static class LogFormatter
    extends Formatter {
        @Override
        public String format(LogRecord record) {
            String stackTrace = "";
            Throwable thrown = record.getThrown();
            if (thrown != null) {
                StringWriter stacktraceWriter = new StringWriter();
                PrintWriter writer = new PrintWriter(stacktraceWriter);
                Throwable throwable = null;
                try {
                    thrown.printStackTrace(writer);
                }
                catch (Throwable var7_8) {
                    throwable = var7_8;
                    throw var7_8;
                }
                finally {
                    if (writer != null) {
                        if (throwable != null) {
                            try {
                                writer.close();
                            }
                            catch (Throwable var7_7) {
                                throwable.addSuppressed(var7_7);
                            }
                        } else {
                            writer.close();
                        }
                    }
                }
                stackTrace = stacktraceWriter.toString();
            }
            return "\t" + record.getMessage() + "\n" + stackTrace;
        }
    }

}

