package com.sdnelson.msc.research.lcf4j.nodemgmt.core;

import com.sdnelson.msc.research.lcf4j.core.NodeData;
import com.sdnelson.msc.research.lcf4j.core.NodeClusterMessage;
import com.sdnelson.msc.research.lcf4j.core.NodeRegistry;
import org.apache.log4j.Logger;

public class ClusterManager {

    final static org.apache.log4j.Logger logger = Logger.getLogger(ClusterManager.class);

    public static void resolveNodeDataMessage(NodeClusterMessage nodeClusterMessage) {
        if(NodeRegistry.getTimestamp().before(nodeClusterMessage.getTimestamp())){
            for (NodeData nodeData : nodeClusterMessage.getNodeDataList()) {
                logger.info("[Adding node data to Registry " + nodeData +"]");
                NodeRegistry.addActiveNode(nodeData);
            }
        } else {
            logger.info("[Registry : " + NodeRegistry.getTimestamp().getTime() + "  > NodeClusterMessage : "
                    + nodeClusterMessage.getTimestamp().getTime() + "]");
            logger.info("[Registry contains more recent data, ignoring the nodeClusterMessage.]");
        }

    }
}
