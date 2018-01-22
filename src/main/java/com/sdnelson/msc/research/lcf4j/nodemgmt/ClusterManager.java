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
        final NodeData nodeData = nodeClusterMessage.getNodeData();
        if (!nodeData.getNodeName().equals(ClusterConfig.getNodeServerName())) {
                logger.debug("[Adding node update data to the Registry " + nodeData + "]");
                NodeRegistry.addActiveNode(nodeData);
        }
    }

    public static void resolveResponseDataMessage(ResponseClusterMessage responseClusterMessage) {
        logger.debug("[Adding node data from server to the Registry " + responseClusterMessage.getNodeData() +"]");
        NodeRegistry.addActiveNode(responseClusterMessage.getNodeData());
    }

    public static boolean resolveRequestNodeData(RequestClusterMessage requestClusterMessage) {
        logger.debug("[Adding node data from client to the Registry " + requestClusterMessage.getNodeData() +"]");
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
        logger.debug("[Resolving node data received : " + foreignNode +"]");
        logger.debug("[Resolving node data local : " + localNode + "]");
        //Check for same node client connected to the server
        if(!localNode.equals(foreignNode)){
            logger.debug("[Conflict found for the node : " + localNode.getNodeName() + ", marking as PASSIVE.");
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
