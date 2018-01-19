package com.lcf4j.app;

import com.sdnelson.msc.research.lcf4j.Lcf4jCache;
import com.sdnelson.msc.research.lcf4j.Lcf4jCluster;
import org.apache.log4j.Logger;

import java.util.UUID;

public class Application {

    final static org.apache.log4j.Logger logger = Logger.getLogger(Lcf4jCluster.class);

    public static void main(String[] args) {
        Lcf4jCluster lcf4jCluster = new Lcf4jCluster();
        Lcf4jCache lcf4jCache = new Lcf4jCache();
        try {
            lcf4jCluster.startCluster();
            lcf4jCache.addToCache(lcf4jCluster.getServerNodeData().getNodeName(), "Active" );
            while(true) {
                lcf4jCache.addToCache("UUID", UUID.randomUUID().toString());
                Thread.sleep(10000);
            }
        } catch (InterruptedException e) {
            logger.error("Error occurred while starting the node server");
            System.exit(0);
        }
    }
}