package edu.bu.met622;

/**********************************************************************************************************************
 * Main entry point
 *
 * @author Michael Lewis
 *********************************************************************************************************************/
public class Main {

    public static void main(String[] args) {
        Builder builder = new Builder();
        builder.startMessage();
        builder.build();
        builder.cleanup();
        builder.endMessage();
    }
}
