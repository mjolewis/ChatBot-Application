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

    private ArrayList<String> fileContainer;
    private String fileName;
    private File outputFile;
    private StringBuilder stringBuilder;
    private BufferedReader reader;
    private BufferedWriter writer;

    /**
     * Initialize a FilerMerger
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this FileMerger
     */
    public FileMerger() {
        fileContainer = new ArrayList<>();
        fileName = Constants.DEFAULT_FILE_NAME;
        stringBuilder = new StringBuilder();
        reader = null;
        writer = null;
    }

    /**
     * Initialize a FilerMerger
     *
     * @param fileContainer Contains file paths for each file that will be merged
     * @param fileName      The name and type of the master file
     * @throws OutOfMemoryError Indicates insufficient memory for this FilerMerger
     */
    public FileMerger(ArrayList<String> fileContainer, String fileName) {
        this.fileContainer = fileContainer;
        this.fileName = fileName;
        stringBuilder = new StringBuilder();
        reader = null;
        writer = null;
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
        try {
            int x = 0;
            for (String currentFile : fileContainer) {
                x++;                                                      // Track which file is being processed

                file  = new File(currentFile);
                reader = new BufferedReader(new FileReader(file));

                if (x == 1) {                                             // Process first file
                    while ((fileContent = reader.readLine()) != null) {

                        // Only include a closing root tag when processing the last file
                        if (!(fileContent.contains(Constants.END_OF_FIRST_FILE))) {
                            stringBuilder.append(fileContent);
                        }
                    }
                } else if (x > 1 && x < fileContainer.size()) {           // Process [second file, last file)

                    reader.readLine();                                    // Avoid processing a duplicate Prolog
                    reader.readLine();                                    // Avoid processing a duplicate Prolog
                    reader.readLine();                                    // Avoid processing the opening root tag

                    while ((fileContent = reader.readLine()) != null) {

                        // Only include a closing root tag when processing the last file
                        if (!(fileContent.contains(Constants.END_OF_FIRST_FILE))) {
                            stringBuilder.append(fileContent);
                        }
                    }
                } else {                                                  // Process last file
                    reader.readLine();                                    // Avoid processing a duplicate Prolog
                    reader.readLine();                                    // Avoid processing a duplicate Prolog
                    reader.readLine();                                    // Avoid processing the opening root tag

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
        try {
            outputFile = new File(fileName);
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

    /**
     * Accessor method that returns the file container
     *
     * @return An ArrayList of file paths
     */
    public ArrayList<String> getFileContainer() {
        return fileContainer;
    }

    /**
     * Mutator method that assigns an ArrayList of file paths
     *
     * @param fileContainer A container of file paths
     */
    public void setFileContainer(ArrayList<String> fileContainer) {
        this.fileContainer = fileContainer;
    }

    /**
     * Accessor method that returns the name of the master file
     *
     * @return The name of the file that contains all the merged documents
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Mutator method that sets the name of the file to the specified argument
     *
     * @param fileName The name and type of the file
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "FileMerger{" +
                "fileContainer=" + fileContainer +
                ", fileName='" + fileName + '\'' +
                ", outputFile=" + outputFile +
                ", stringBuilder=" + stringBuilder +
                ", reader=" + reader +
                ", writer=" + writer +
                '}';
    }
}
