package com.sdnelson.msc.research.lcf4j.reference.http;

import com.sdnelson.msc.research.lcf4j.util.ClusterConfig;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class App {

    final static Logger logger = Logger.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        new App().initConfigProperties();

        for (int i = 1; i < 2 ; i++) {
            new UptimeServer().startServer(8080);
        }

//        for (int i = 1; i < 3; i++) {
//            new Client("localhost", 8080);
//        }

//        logger.info("Nodes list found - " + nodeList);
//        for (String node : nodeList) {
//            node = node.trim();
//            logger.info("Node " + node);
//            new Client(
//                    node.split(":")[0].trim(), Integer.valueOf(node.split(":")[1].trim()).intValue());
//        }
//        logger.info("ok.");

    }

    private void initConfigProperties() {
        logger.debug("Loading Properties to Config ...");
        try{
            ClusterConfig.initClusterConfig();
        } catch (Exception ex){
            logger.error("Invalid Properties Found, Error Loading the Property File ...");
            System.exit(0);
        }
        logger.info("Successfully Loaded the Properties to Config ...");
    }
}
