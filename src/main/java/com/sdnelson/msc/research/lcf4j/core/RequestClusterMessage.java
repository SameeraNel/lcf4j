package com.sdnelson.msc.research.lcf4j.core;


import java.io.Serializable;
import java.util.Calendar;

public class RequestClusterMessage implements Serializable, ClusterMessage  {

    private Calendar timestamp;
    private NodeData nodeData;

    public RequestClusterMessage(NodeData nodeData) {
        this.nodeData = nodeData;
        timestamp = Calendar.getInstance();
    }

    public Calendar getTimestamp() {
        return timestamp;
    }

    public NodeData getNodeData() {
        return nodeData;
    }
}
