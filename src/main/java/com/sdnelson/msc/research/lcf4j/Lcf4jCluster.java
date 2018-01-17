package com.sdnelson.msc.research.lcf4j;

import com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.client.WebSocketClient;
import com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.server.WebSocketServer;
import com.sdnelson.msc.research.lcf4j.util.ClusterConfig;
import org.apache.log4j.Logger;

public class Lcf4jCluster {

    final static org.apache.log4j.Logger logger = Logger.getLogger(Lcf4jCluster.class);

    public void startCluster() throws InterruptedException {
        logger.info("Initiating LCF4J Cluster Framework ...");
        initConfigProperties();
        startNodeServer();
        startNodeClients();
        logger.info("LCF4J Cluster Framework Started Successfully ...");
    }

    private void startNodeClients() {
        final int nodeCount = ClusterConfig.getClusterNodeList().size();
        WebSocketClient webSocketClient = new WebSocketClient(nodeCount);
        try {
            for (int i = 0; i < nodeCount; i++) {
                if(checkCurrentNode(i)){
                    continue;
                }
                webSocketClient.startClient(
                        ClusterConfig.getClusterNodeList().get(i).getHost(),
                        ClusterConfig.getClusterNodeList().get(i).getPort());
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            logger.error("Error occurred while starting the node client @ " +
                    ClusterConfig.getNodeServerName()+ ":" + ClusterConfig.getNodeServerPort() + " ...");
        }
    }

    private boolean checkCurrentNode(int i) {
        return ClusterConfig.getClusterNodeList().get(i).getHost().equals(ClusterConfig.getNodeServerName())
                && ClusterConfig.getClusterNodeList().get(i).getPort() == ClusterConfig.getNodeServerPort();
    }

    private void startNodeServer() {
        WebSocketServer webSocketServer = new WebSocketServer();
        try {
            webSocketServer.startServer(
                    ClusterConfig.getNodeServerName(), ClusterConfig.getNodeServerPortSsl());
            Thread.sleep(2000);
        } catch (Exception e) {
            logger.error("Error occurred while starting the node server @ " +
                    ClusterConfig.getNodeServerName()+ ":" + ClusterConfig.getNodeServerPortSsl() + " ...");
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
