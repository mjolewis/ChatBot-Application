package edu.bu.met622.output;

import edu.bu.met622.resources.ApplicationConfig;

import java.io.*;

/**********************************************************************************************************************
 * Log application events
 *
 * @author Michael Lewis
 * @version November 22, 2020 - Kickoff
 *********************************************************************************************************************/
public class Logger {
    private static Logger logger = null;
    private File runtimeLog;
    private File errorLog;

    /**
     * Initialize a new Logger instance
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this new Logger
     */
    private Logger() {
        runtimeLog = new File(ApplicationConfig.RUNTIME_LOG);
        errorLog = new File(ApplicationConfig.ERROR_LOG);
    }

    /**
     * Static factory method to create a Logger instance while avoiding the unnecessary expense of creating duplicate
     * objects
     *
     * @return A Logger instance
     */
    public static Logger getInstance() {
        if (logger == null) {
            logger = new Logger();
        }

        return logger;
    }

    /**
     * Log the runtime of the specified search type
     *
     * @param searchType The type of search that was performed
     * @param runtime    The total runtime of the current search
     */
    public void runtime(String searchType, double runtime) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(runtimeLog, true))) {
            writer.write(searchType + ApplicationConfig.COMMA_DELIMITER + runtime + ApplicationConfig.NEW_LINE_SEPARATOR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
