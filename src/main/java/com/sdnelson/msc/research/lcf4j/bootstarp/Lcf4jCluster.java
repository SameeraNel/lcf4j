package com.sdnelson.msc.research.lcf4j.bootstarp;

import com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.client.WebSocketClient;
import com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.server.WebSocketServer;
import com.sdnelson.msc.research.lcf4j.util.ClusterConfig;
import org.apache.log4j.Logger;


public class Lcf4jCluster {

    final static org.apache.log4j.Logger logger = Logger.getLogger(Lcf4jCluster.class);

    public void startCluster(){
        logger.info("Initiating LCF4J Cluster Framework ...");
        initConfigProperties();
        startNodeServer();
        startNodeClients();

    }

    private void startNodeClients() {
        WebSocketClient webSocketClient = new WebSocketClient();
        try {
            for (int i = 0; i < ClusterConfig.getClusterNodeList().size(); i++) {
                webSocketClient.startClient(
                        ClusterConfig.getClusterNodeList().get(i).getHost(),
                        ClusterConfig.getClusterNodeList().get(i).getPort());
            }

        } catch (Exception e) {
            logger.error("Error occured while starting the node client @ " +
                    ClusterConfig.getNodeServerName()+ ":" + ClusterConfig.getNodeServerPort() + " ...");
        }
    }

    private void startNodeServer() {
        WebSocketServer webSocketServer = new WebSocketServer();
        try {
            webSocketServer.startServer();
        } catch (Exception e) {
            logger.error("Error occured while starting the node server @ " +
                    ClusterConfig.getNodeServerName()+ ":" + ClusterConfig.getNodeServerPort() + " ...");
            System.exit(0);
        }
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
