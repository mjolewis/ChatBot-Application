package edu.bu.met622.output;

import edu.bu.met622.resources.ApplicationConfig;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**********************************************************************************************************************
 * Log application events
 *
 * @author Michael Lewis
 * @version November 22, 2020 - Kickoff
 *********************************************************************************************************************/
public class Log {
    private static Log log = null;
    private File runtimeLog;
    private File errorLog;

    /**
     * Initialize a new Log instance
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this new Log
     */
    private Log() {
        runtimeLog = new File(ApplicationConfig.RUNTIME_LOG);
        errorLog = new File(ApplicationConfig.ERROR_LOG);
    }

    /**
     * Static factory method to create a Logger instance while avoiding the unnecessary expense of creating duplicate
     * objects
     *
     * @return A Log instance
     */
    public static Log getInstance() {
        if (log == null) {
            log = new Log();
        }

        return log;
    }

    /**
     * Log application runtime information
     *
     * @param databaseType The type of search that was performed
     * @param keyword      The keyword that was searched
     * @param runtime      The total runtime of the current search
     */
    public void runtime(String databaseType, String keyword, double runtime) {
        LocalDateTime timeStamp = LocalDateTime.now();
        String formattedTimeStamp = timeStamp.format(DateTimeFormatter.ofPattern(ApplicationConfig.DATE_FORMAT));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(runtimeLog, true))) {
            writer.write( formattedTimeStamp + ApplicationConfig.COMMA_DELIMITER
                    + databaseType + ApplicationConfig.COMMA_DELIMITER
                    + keyword + ApplicationConfig.COMMA_DELIMITER
                    + runtime + ApplicationConfig.NEW_LINE_SEPARATOR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
