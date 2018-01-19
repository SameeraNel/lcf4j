package com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.message;


import com.sdnelson.msc.research.lcf4j.core.*;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;

// Holder Object for node data and cache data
public class RequestClusterMessage implements ClusterMessage {

    private Calendar nodeTimestamp;
    private Calendar cacheTimestamp;
    private NodeData nodeData;
    private HashMap<String, String> cacheMapData;

    public RequestClusterMessage(final Calendar nodeTimestamp,final Calendar cacheTimestamp,final  NodeData nodeData,final HashMap<String, String> cacheMapData) {
        this.nodeTimestamp = nodeTimestamp;
        this.cacheTimestamp = cacheTimestamp;
        this.nodeData = nodeData;
        this.cacheMapData = cacheMapData;
    }

    public Calendar getNodeTimestamp() {
        return nodeTimestamp;
    }

    public NodeData getNodeData() {
        return nodeData;
    }

    public HashMap<String, String> getCacheMapData() {
        return cacheMapData;
    }

    public Calendar getCacheTimestamp() {
        return cacheTimestamp;
    }
}
