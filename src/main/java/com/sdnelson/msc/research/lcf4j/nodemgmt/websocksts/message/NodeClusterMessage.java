package com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.message;

import com.sdnelson.msc.research.lcf4j.core.NodeData;

import java.util.Calendar;

public class NodeClusterMessage implements ClusterMessage {

    private Calendar timestamp;
    private NodeData nodeData;

    public NodeClusterMessage(Calendar timestamp, NodeData nodeData) {
        this.nodeData = nodeData;
        this.timestamp = timestamp;
        this.timestamp = Calendar.getInstance();
    }

    public Calendar getTimestamp() {
        return timestamp;
    }

    public NodeData getNodeData() {
        return nodeData;
    }
}
