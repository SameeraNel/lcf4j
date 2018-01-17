package com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.message;

import com.sdnelson.msc.research.lcf4j.core.*;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

public class NodeClusterMessage implements Serializable, ClusterMessage {

    private Calendar timestamp;
    private List<NodeData> nodeDataList;

    public NodeClusterMessage(List<NodeData> nodeDataList) {
        this.nodeDataList = nodeDataList;
        timestamp = Calendar.getInstance();
    }

    public Calendar getTimestamp() {
        return timestamp;
    }

    public List<NodeData> getNodeDataList() {
        return nodeDataList;
    }
}
