package edu.bu.met622;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**********************************************************************************************************************
 * Main entry point
 *
 * @author Michael Lewis
 *********************************************************************************************************************/
@SpringBootApplication
public class ChatBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatBotApplication.class, args);

//        Builder builder = new Builder();
//        builder.startMessage();
//        builder.build();
//        builder.cleanup();
//        builder.endMessage();
    }
}