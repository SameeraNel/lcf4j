package com.sdnelson.msc.research.lcf4j.nodemgmt;

import com.sdnelson.msc.research.lcf4j.core.*;
import com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.message.ConflictClusterMessage;
import com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.message.NodeClusterMessage;
import com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.message.RequestClusterMessage;
import com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.message.ResponseClusterMessage;
import com.sdnelson.msc.research.lcf4j.util.ClusterConfig;
import org.apache.log4j.Logger;

public class ClusterManager {

    final static Logger logger = Logger.getLogger(ClusterManager.class);

    public static void resolveNodeDataMessage(NodeClusterMessage nodeClusterMessage) {
        for (NodeData nodeData : nodeClusterMessage.getNodeDataList()) {
            if (!nodeData.getNodeName().equals(ClusterConfig.getNodeServerName()) &&
                    (!NodeRegistry.contains(nodeData.getNodeName()) ||
                    nodeData.getLastUpdated().after(NodeRegistry.getNodeData(nodeData.getNodeName()).getLastUpdated()))) {
                logger.info("[Adding node data to Registry " + nodeData + "]");
                NodeRegistry.addActiveNode(nodeData);
            } else{
                logger.info("[Registry : " + NodeRegistry.getNodeData(nodeData.getNodeName()).getLastUpdated().getTime() + "  > NodeClusterMessage : "
                        + nodeData.getLastUpdated().getTime() + "]");
                logger.info("[Registry contains more recent data, ignoring the nodeClusterMessage...]");
            }
        }
    }

    public static void resolveResponseDataMessage(ResponseClusterMessage responseClusterMessage) {
        logger.info("[Adding node data to Registry " + responseClusterMessage.getNodeData() +"]");
        NodeRegistry.addActiveNode(responseClusterMessage.getNodeData());
    }

    public static boolean resolveRequestDataMessage(RequestClusterMessage requestClusterMessage) {
        logger.info("[Adding node data to Registry " + requestClusterMessage.getNodeData() +"]");
        //Node Name conflict found
        if(ClusterConfig.getNodeServerName().equals(requestClusterMessage.getNodeData().getNodeName())){
            return false;
        } else {
            NodeRegistry.addActiveNode(requestClusterMessage.getNodeData());
            return true;
        }
    }

    public static void resolveConflictDataMessage(ConflictClusterMessage conflictClusterMessage) {
        NodeData localNode = NodeRegistry.getServerNodeData();
        NodeData foreignNode = conflictClusterMessage.getNodeData();
        logger.info("[Resolving node data received : " + foreignNode +"]");
        logger.info("[Resolving node data local : " + localNode + "]");
        //Check for same node client connected to the server
        if(!localNode.equals(foreignNode)){
            logger.info("[Conflict found for the node : " + localNode.getNodeName() + ", marking as PASSIVE.");
            NodeRegistry.markNodeStatus(localNode.getNodeName(), NodeStatus.PASSIVE);
        }
    }

    public static void resolveUnRegistered(String clientNodeName) {
        final NodeData nodeData = NodeRegistry.getNodeData(clientNodeName);
        if(nodeData != null){
            NodeRegistry.markNodeStatus(nodeData.getNodeName(), NodeStatus.OFFLINE);
        }
    }

    public static void resolveUnRegisteredByHostName(String clientNodeName) {
        final NodeData nodeData = NodeRegistry.getNodeByRemoteHostName(clientNodeName);
        if(nodeData != null){
            NodeRegistry.markNodeStatus(nodeData.getNodeName(), NodeStatus.OFFLINE);
        }
    }
}
