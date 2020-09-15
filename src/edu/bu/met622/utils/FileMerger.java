package edu.bu.met622.utils;

import edu.bu.met622.sharedresources.Constants;

import java.io.*;
import java.util.ArrayList;

/**********************************************************************************************************************
 * Merge documents into one master file
 *
 * @author Michael Lewis
 *********************************************************************************************************************/
public class FileMerger {

    private ArrayList<String> filePaths;
    private String mergedFileName;
    private File outputFile;
    private StringBuilder stringBuilder;

    /**
     * Initialize a FilerMerger
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this FileMerger
     */
    public FileMerger() {
        filePaths = new ArrayList<>();
        mergedFileName = Constants.OUTPUT_FILE;
        stringBuilder = new StringBuilder();
    }

    /**
     * Initialize a FilerMerger
     *
     * @param filePaths      Contains file paths for each file that will be merged
     * @param mergedFileName The name and type of the master file
     * @throws OutOfMemoryError Indicates insufficient memory for this FilerMerger
     */
    public FileMerger(ArrayList<String> filePaths, String mergedFileName) {
        this.filePaths = filePaths;
        this.mergedFileName = mergedFileName;
        stringBuilder = new StringBuilder();
    }

    /**
     * Merge source files into one master file
     *
     * @throws IOException Indicates an I/O error has occurred
     */
    public void merge() throws IOException {
        readFile();                                                       // Process each file in the container
        writeFile();                                                      // Merge files into master
    }

    /*
     * Helper method that reads the content of multiple files
     *
     * @throws IOException Indicates an I/O error has occurred
     * @throws OutOfMemoryError Indicates insufficient memory
     */
    private void readFile() throws IOException {
        File file;
        String fileContent;
        BufferedReader reader = null;
        try {
            int x = 0;
            for (String currentFile : filePaths) {
                x++;                                                      // Track which file is being processed

                file = new File(currentFile);
                reader = new BufferedReader(new FileReader(file));

                if (x == 1) {                                             // Process first file
                    while ((fileContent = reader.readLine()) != null) {

                        // Only include a closing root tag when processing the last file
                        if (!(fileContent.contains(Constants.CLOSING_ROOT_TAG))) {
                            stringBuilder.append(fileContent);
                        }
                    }
                } else if (x > 1 && x < filePaths.size()) {               // Process [second file, last file)
                    advanceRows(reader, 3);
                    while ((fileContent = reader.readLine()) != null) {

                        // Only include a closing root tag when processing the last file
                        if (!(fileContent.contains(Constants.CLOSING_ROOT_TAG))) {
                            stringBuilder.append(fileContent);
                        }
                    }
                } else {                                                  // Process last file
                    advanceRows(reader, 3);
                    while ((fileContent = reader.readLine()) != null) {   // Includes closing root tag
                        stringBuilder.append(fileContent);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            reader.close();
        }
    }

    /*
     * Helper method that writes the content of multiple files into a single master file
     *
     * @throws IOException Indicates an I/O error has occurred
     */
    private void writeFile() throws IOException {
        BufferedWriter writer = null;
        try {
            outputFile = new File(mergedFileName);
            writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(stringBuilder.toString());
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /*
     * Advances N rows in the document and discards them. This method can be used to avoid processing duplicate XML
     * tags. For example, a well-formed XML document can only have one prolog tag, one opening root tag, and one
     * closing root tag.
     */
    private void advanceRows(BufferedReader reader, int rows) throws IOException {
        for (int i = 0; i < rows; ++i) {
            reader.readLine();
        }
    }

    /**
     * Accessor method that returns the file container
     *
     * @return An ArrayList of file paths
     */
    public ArrayList<String> getFilePaths() {
        return filePaths;
    }

    /**
     * Mutator method that assigns an ArrayList of file paths
     *
     * @param filePaths A container of file paths
     */
    public void setFilePaths(ArrayList<String> filePaths) {
        this.filePaths = filePaths;
    }

    /**
     * Accessor method that returns the name of the master file
     *
     * @return The name of the file that contains all the merged documents
     */
    public String getMergedFileName() {
        return mergedFileName;
    }

    /**
     * Mutator method that sets the name of the file to the specified argument
     *
     * @param mergedFileName The name and type of the file
     */
    public void setMergedFileName(String mergedFileName) {
        this.mergedFileName = mergedFileName;
    }

    @Override
    public String toString() {
        return "FileMerger{" +
                ", fileName='" + mergedFileName + '\'' +
                ", outputFile=" + outputFile +
                '}';
    }
}
