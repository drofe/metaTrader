package org.bergefall.common.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.SimpleDateFormat;

public class RotatingFileHandler implements FileHandler {
    
    private static final SimpleDateFormat cSdf = new SimpleDateFormat("yyyy-MM-dd--HH.mm.ss.SSSZ");
    private static final int cBufSize = 4096;
    private final String loginLogfile;
    private final String logDir;
    private String timestamp = cSdf.format(new java.util.Date());
    private File file;
    private boolean flushOnEachWrite;
    private long limit;
    private MeteredBufferedWriter meteredWriter;
    private boolean failedInitialization;
    
    /**
     * 
     * @param logDir
     * @param flushOnEachWrite
     * @param fileSizeLimit File size before rotating (in kB).
     * @param filePrefix
     */
    public RotatingFileHandler(String logDir, 
        boolean flushOnEachWrite,
        long fileSizeLimit,
        final String filePrefix) {
        if (!logDir.endsWith("\\") && !logDir.endsWith("/")) {
            logDir = logDir + "/";
        }
        this.logDir = logDir;
        File tTmpFile = new File(logDir);
        if (!tTmpFile.isDirectory()) {
            failedInitialization = true;
            loginLogfile = "";
            return;
        }
        loginLogfile = logDir + filePrefix;
        this.flushOnEachWrite = flushOnEachWrite;
        this.limit = fileSizeLimit * 1024;
        this.file = constructFileName();
        try {
            open(file);
        }
        catch (Throwable e) {
            failedInitialization = true;
        }
    }
    
    public boolean failedInit() {
        return failedInitialization;
    }
    
    public String getLogDir() {
        return logDir;
    }
    
    public void close() {
        try {
            meteredWriter.close();
        } 
        catch (IOException e) {
            //By design.
        }
    }
    
    public synchronized void write(String record) throws IOException {
        if (null == record || record.isEmpty()) {
            return;
        }

        meteredWriter.write(record);
        meteredWriter.write(System.lineSeparator());
        if (flushOnEachWrite) {
            meteredWriter.flush();
        }
        if (limit > 0 && meteredWriter.mWritten >= limit) {
            performRotate();
        }
    }
    
    private void open(File fName) throws IOException {
        OutputStreamWriter tOSW = new OutputStreamWriter(new FileOutputStream(fName), StandardCharsets.UTF_8);
        meteredWriter = new MeteredBufferedWriter(tOSW);
    }
    
    // Rotate the set of output files
    private synchronized void rotate() {

        try {
            meteredWriter.close();
        }
        catch (IOException e1) {
            //By design.
        }
        try {
            file = constructFileName();
            if (file == null) {
                System.err.println("Could not construct file name");
            }
            else {
                open(file);
            }
        }
        catch (IOException e) {
            failedInitialization = true;
        }
    }

    private void performRotate() {
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
                rotate();
                return null;
            }
        });
    }
    
    private File constructFileName() {

        
        String tFileName = loginLogfile + timestamp + ".log";

        File tFName = new java.io.File(tFileName);
        int i = 1;

        while (tFName.exists() && i < 1000) {
            try {
                Thread.sleep(5);
            }
            catch (InterruptedException e) {
                // Intentionally left empty.
            }
            tFileName = loginLogfile + timestamp + "-" + i + ".log";
            tFName = new java.io.File(tFileName);
            i++;
        }
        return tFName;
    }
    
    /**
     * A meteredBufferedWriter is a subclass of BufferedWriter that
     *   (a) calls super implementation of write(String)
     *   (b) keeps track of how many bytes have been written
     */
    private static class MeteredBufferedWriter extends BufferedWriter {
        int mWritten;

        MeteredBufferedWriter(Writer pOut) {
            super(pOut, cBufSize);
            mWritten = 0;
        }

        @Override
        public void write(String pStr) throws IOException {
            mWritten += pStr.length();
            super.write(pStr);
        }
    }
}