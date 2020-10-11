package edu.bu.met622.utils;

import edu.bu.met622.sharedresources.Constants;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**********************************************************************************************************************
 * Storage utility
 *
 * @author Michael Lewis
 * @version September 18, 2020 Kick-Off
 *********************************************************************************************************************/
public class Storage {

    private Map<String, ArrayList<String>> searchHistory;       // Key: Search Parameter; Value: Frequency, Timestamp
    private File file;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;

    /**
     * Initialize a new Storage object
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this new Storage object
     * @note If the application has previously stored search history to disk, then initializing Storage will put the
     *         history back into memory
     */
    public Storage() {
        searchHistory = new HashMap<>();
        String fileName = Constants.SEARCH_HISTORY;
        file = new File(fileName);
        bufferedWriter = null;
        bufferedReader = null;

        // If search history exists on disk, put it back into memory
        if (file.exists()) {
            try {
                restoreHistory();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Provides non-volatile storage for search history, search frequency, and time of search
     *
     * @param searchParam The search parameter entered by the user
     * @param timestamp   The date and time of the search
     * @throws IOException Indicates a failed or interrupted I/O operation
     */
    public void saveToDisk(String searchParam, String timestamp) throws IOException {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            bufferedWriter = new BufferedWriter(new FileWriter(file, true));
            bufferedWriter.write(searchParam.toLowerCase() + Constants.COMMA_DELIMITER +
                    timestamp + Constants.NEW_LINE_SEPARATOR);
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
     * @param timestamp   The date and time of the search
     */
    public void saveToMemory(String searchParam, String timestamp) {

        if (!searchHistory.containsKey(searchParam)) {                         // If the key isn't in the collection
            ArrayList<String> values = new ArrayList<>();
            values.add(0, String.valueOf(1));                                   // Add initial frequency
            values.add(timestamp);                                             // Add time stamp of the search
            searchHistory.put(searchParam.toLowerCase(), values);
        } else {                                                               // Otherwise the key is in the collection
            int frequency = Integer.parseInt(searchHistory.get(searchParam.toLowerCase()).get(0));
            searchHistory.get(searchParam.toLowerCase()).set(0, String.valueOf(++frequency));
            searchHistory.get(searchParam.toLowerCase()).add(timestamp);       // Add time stamp of the search
        }
    }

    /**
     * Accessor method that returns the container of search history data. This includes the search parameter, the
     * frequency, and timestamps
     *
     * @return The users search history
     * @note Each entry in the container stores the time stamps for all searches
     */
    public Map<String, ArrayList<String>> getSearchHistory() {
        return searchHistory;
    }

    /**
     * Prints the search history to the console
     */
    public void print() {
        for (Map.Entry<String, ArrayList<String>> entry : searchHistory.entrySet()) {
            System.out.println(entry);
        }
    }

    //*****************************************************************************************************************
    // Helper methods to repopulate the data collection if it already exists on disk
    //*****************************************************************************************************************

    /*
     * If search history exists on disk, put it back into memory
     */
    private void restoreHistory() throws IOException {
        String[] items;
        String line;

        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            while ((line = bufferedReader.readLine()) != null) {
                items = line.split(Constants.COMMA_DELIMITER);                 // K,V pair split by "," in CSV file

                if (!searchHistory.containsKey(items[0])) {                    // If the key isn't in the collection
                    ArrayList<String> values = new ArrayList<>();
                    values.add(0, String.valueOf(1));                    // Add initial frequency
                    values.add(1, items[1]);                            // Add time stamp of the first search
                    searchHistory.put(items[0], values);                       // Rebuild the collection
                } else {                                                       // Otherwise the key is in the collection
                    int frequency = Integer.parseInt(searchHistory.get(items[0]).get(0));
                    searchHistory.get(items[0]).set(0, String.valueOf(++frequency));
                    searchHistory.get(items[0]).add(items[1]);                 // Add time stamp of subsequent searches
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            bufferedReader.close();
        }
    }
}