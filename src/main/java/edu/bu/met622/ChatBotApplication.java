package edu.bu.met622;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**********************************************************************************************************************
 * Main entry point
 *
 * @author Michael Lewis
 *********************************************************************************************************************/
@SpringBootApplication
public class ChatBotApplication {

    public static void main(String[] args) {

        Builder builder = new Builder();
        builder.startMessage();
        builder.build();

        //SpringApplication.run(ChatBotApplication.class, args);
        SpringApplicationBuilder springBuilder = new SpringApplicationBuilder(ChatBotApplication.class);
        springBuilder.headless(false);
        ConfigurableApplicationContext context = springBuilder.run(args);
    }
}