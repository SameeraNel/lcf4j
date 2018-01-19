package com.sdnelson.msc.research.lcf4j;

import com.sdnelson.msc.research.lcf4j.core.NodeData;
import com.sdnelson.msc.research.lcf4j.core.NodeStatus;
import com.sdnelson.msc.research.lcf4j.nodemgmt.NodeRegistry;
import com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.client.WebSocketClient;
import com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.server.WebSocketServer;
import com.sdnelson.msc.research.lcf4j.util.ClusterConfig;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class Lcf4jCluster {

    final static org.apache.log4j.Logger logger = Logger.getLogger(Lcf4jCluster.class);

    private static List<WebSocketClient> clientList = new ArrayList<>();

    public void startCluster() throws InterruptedException {
        logger.info("Initiating LCF4J Cluster Framework ...");
        initConfigProperties();
        startNodeServer();
        startNodeClients();
        if(NodeStatus.ACTIVE.equals(NodeRegistry.getServerNodeStatus())){
            NodeRegistry.markNodeStatus(ClusterConfig.getNodeServerName(), NodeStatus.ONLINE);
        }
        logger.info("LCF4J Cluster Framework Started Successfully ...");
    }

    private void startNodeClients() {
        final int nodeCount = ClusterConfig.getClusterNodeList().size();
        try {
            for (int i = 0; i < nodeCount; i++) {
                if(checkCurrentNode(i)){
                    continue;
                }
                WebSocketClient webSocketClient = new WebSocketClient(nodeCount);
                clientList.add(webSocketClient);
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

    /**
     **     Return the current active node count of the cluster
     **/
    public int getActiveNodeCount(){
        return NodeRegistry.getActiveNodeCount();
    }

    /**
     **     Return the current active node list of the cluster
     **/
    public List<NodeData> getActiveNodeList(){
        return NodeRegistry.getActiveNodeList();
    }

    /**
     **     Return the current active node map of the cluster
     **/
    public Map<String, NodeData> getActiveNodeMap(){
        return NodeRegistry.getActiveNodeMap();
    }

    /**
     **     Return the last updated time of the node registry
     **/
    public Calendar getLastUpdatedTime(){
        return NodeRegistry.getTimestamp();
    }

    /**
     **     Return the local node data
     **/
    public NodeData getServerNodeData(){
        return NodeRegistry.getServerNodeData();
    }

    /**
     **     Return the current active node name list of the cluster
     **/
    public List<String> getActiveServerNameList(){
        return NodeRegistry.getNodeKeyList();
    }

    public static List<WebSocketClient> getClientList() {
        return clientList;
    }
}
