/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.integral.ds.tradeepafieldupdater.temp;

import com.integral.ds.s3.S3StreamReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;

/**
 * S3 Rates zip stream reader.
 *
 * @author Rahul Bhattacharjee
 */
public class S3RatesReader2 {

//    private static final Logger LOGGER = Logger.getLogger(S3RatesReader.class);
    private final S3StreamReader s3StreamReader = new S3StreamReader();

    private static final DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd-HH", Locale.ENGLISH);
    private static final DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    private static File zipFileRef;
    int fileCnt = 0;
    DateTime globalDate;

    /**
     * Fetches rates for provider , stream , currency pair for a given time
     * period.
     *
     * @param provider
     * @param stream
     * @param fromTime
     * @param endTime
     * @param ccyPair
     * @return
     */
    public String getMinuteClob(DateTime timeIn, ZipFile zip) {
        InputStream entryIn = null;
        String minuteClob = "";
        try {
            ZipEntry ze = null;
            String zipName = zip.getName();
            String dateStr = df1.format(timeIn.toDate());
            int min = timeIn.getMinuteOfHour();
            String entryName = "processedLog_" + dateStr + "_" + min + ".csv";
            Enumeration enum1 = zip.entries();
            List<ZipEntry> list = Collections.list(enum1);
            ze = zip.getEntry(entryName);
            entryIn = zip.getInputStream(ze);
            minuteClob = IOUtils.toString(entryIn, "UTF-8");
//            entryIn.close();
        } catch (Exception ex) {
            Log.error("minute file not found");
            System.out.println(ex.getMessage());
        } finally {
            try {
                if (entryIn != null) {
                    entryIn.close();                  
                }
            } catch (Exception ex) {
                ex.getMessage();
            }
        }
        return minuteClob;
    }

    public ZipFile getZipFile(String zipFileName) {
        ZipFile zip = null;
        try {
            File file = new File(zipFileName);
            System.out.println("********************* file size " + file.length());
            zip = new ZipFile(file, ZipFile.OPEN_READ);
            return zip;
        } catch (IOException ex) {
            Log.error(ex.getMessage());
        }
        return zip;
    }

    public void downloadHourFile(String filePath, String zipFileName) {
        InputStream inputStream = null;
        try {
            
            
            inputStream = s3StreamReader.getInputStream(filePath);
            try (OutputStream output = new FileOutputStream(zipFileName)) {
                byte[] buffer = new byte[8 * 1024]; // Or whatever
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) > 0) {
                    output.write(buffer, 0, bytesRead);
                }
                output.close();
//            inputStream.close();
            } // Or whatever
        } catch (Exception ex) {
            Log.error(ex.getMessage());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ex) {
                Log.error(ex.getMessage());
            }
        }
    }

}
//             Path zipFile = Paths.get(args[0]);
//    FileSystem fileSystem = FileSystems.newFileSystem(zipFile, null);
//
//    File outputFile = File.createTempFile("test", "");
//    Path source = fileSystem.getPath(name);
//    Files.copy(source, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);