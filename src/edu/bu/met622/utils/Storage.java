package edu.bu.met622.utils;

import edu.bu.met622.sharedresources.Constants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**********************************************************************************************************************
 * Non-volatile storage utility
 *
 * @author Michael Lewis
 * @version September 18, 2020 Kick-Off
 *********************************************************************************************************************/
public class Storage {

    private Map<String, ArrayList<Object>> searchHistory;       // Key: Search Parameter; Value: Frequency, Timestamp
    private File file;
    private BufferedWriter bufferedWriter;

    /**
     * Initialize a new DiskStorage object
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this new DiskStorage object
     */
    public Storage() {
        searchHistory = new HashMap<>();
        String fileName = Constants.SEARCH_HISTORY;
        file = new File(fileName);
        bufferedWriter = null;
    }

    /**
     * Provides non-volatile storage for search history, search frequency, and time of search
     *
     * @param searchParam The search parameter entered by the user
     * @param timestamp The date and time of the search
     */
    public void saveToDisk(String searchParam, Timestamp timestamp) throws IOException {
        try {
            if (!file.exists()) { file.createNewFile(); }
            bufferedWriter = new BufferedWriter(new FileWriter(file, true));
            bufferedWriter.write(searchParam + Constants.COMMA_DELIMITER + timestamp + Constants.NEW_LINE_SEPARATOR);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        }
    }

    /**
     * Provides volatile storage for data storage for search history, search frequency, and time of search
     *
     * @param searchParam The search parameter entered by the user
     * @param timestamp The date and time of the search
     */
    public void saveToMemory(String searchParam, Timestamp timestamp) {
        if (!searchHistory.containsKey(searchParam)) {                    // First time the search parameter was entered
            ArrayList<Object> values = new ArrayList<>();
            values.add(0, 1);                              // Store search frequency at index 0
            values.add(timestamp);                                        // Track time stamp of all searches
            searchHistory.put(searchParam, values);
        } else {                                                          // Update frequency; add another timestamp
            int frequency = (int) searchHistory.get(searchParam).get(0);
            searchHistory.get(searchParam).set(0, ++frequency);
            searchHistory.get(searchParam).add(timestamp);
        }
    }

    /**
     * Accessor method that returns the container of search history data. This includes the search parameter, the
     * frequency, and timestamps
     *
     * @return The users search history
     * @note Each entry in the container stores the time stamps for all searches
     */
    public Map<String, ArrayList<Object>> getSearchHistory() {
        return searchHistory;
    }

    /**
     * Prints the search history to the console
     */
    public void printSearchHistory() {
            for (Map.Entry<String, ArrayList<Object>> entry : searchHistory.entrySet()) {
            System.out.println(entry.getKey() + entry.getValue());
        }
    }

}
