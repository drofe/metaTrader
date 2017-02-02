package org.bergefall.common.log;

import java.io.IOException;

public interface FileHandler {

	/**
	 * Check init status.
	 * @return
	 */
	public boolean failedInit();
    
	/**
	 * Get directory to which file is written.
	 * @return
	 */
    public String getLogDir();
    
    /**
     * Close handler and open files.
     */
    public void close();
    
    /**
     * Write string to file. Data will be appended with line feed.
     * @param record
     * @throws IOException 
     */
    public void write(String record) throws IOException;
}
