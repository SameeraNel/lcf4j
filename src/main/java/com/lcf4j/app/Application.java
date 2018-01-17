package com.lcf4j.app;

import com.sdnelson.msc.research.lcf4j.Lcf4jCluster;
import org.apache.log4j.Logger;

public class Application {

    final static org.apache.log4j.Logger logger = Logger.getLogger(Lcf4jCluster.class);

    public static void main(String[] args) {
        Lcf4jCluster lcf4jCluster = new Lcf4jCluster();
        try {
            lcf4jCluster.startCluster();
        } catch (InterruptedException e) {
            logger.error("Error occurred while starting the node server");
            System.exit(0);
        }
    }
}