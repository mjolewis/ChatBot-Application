package edu.bu.met622.controller;

import edu.bu.met622.output.Graph;
import edu.bu.met622.resources.ApplicationConfig;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import javax.swing.*;

/**********************************************************************************************************************
 * A web request handler that plots runtime results of each query
 *
 * @author Michael Lewis
 * @version November 20, 2020 - Kickoff
 *********************************************************************************************************************/
@Controller
public class OutputController {

    /**
     * Initialize a new OutputController
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this new OutputController
     */
    public OutputController() {}

    /**
     * Maps messages to the /graph endpoint by matching the declared patterns to a destination extracted from the
     * message. Sends the response to the /query/graph/response endpoint
     */
    @MessageMapping("/graph")
    @SendTo("/output/graph")
    public void loadGraph() {
        Graph graph = new Graph();

        graph.build();

        graph.setAlwaysOnTop(true);
        graph.pack();
        graph.setSize(ApplicationConfig.CHART_WIDTH, ApplicationConfig.CHART_HEIGHT);
        graph.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        graph.setVisible(true);
    }
}
