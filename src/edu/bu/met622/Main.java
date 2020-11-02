package edu.bu.met622;

/**********************************************************************************************************************
 * Main entry point
 *
 * @author Michael Lewis
 *********************************************************************************************************************/
public class Main {

    public static void main(String[] args) {
        edu.bu.met622.Builder builder = new edu.bu.met622.Builder();
        builder.startMessage();
        builder.build();
        builder.cleanup();
        builder.endMessage();
    }
}
