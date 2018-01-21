package com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.message;


import com.sdnelson.msc.research.lcf4j.config.ConfigData;
import com.sdnelson.msc.research.lcf4j.core.*;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

// Holder Object for node data and cache data
public class RequestClusterMessage implements ClusterMessage {

    private Calendar nodeTimestamp;
    private Calendar cacheTimestamp;
    private Calendar registryTimestamp;
    private NodeData nodeData;
    private Map<String, String> cacheMapData;
    private Map<Integer, ConfigData> configData;

    public RequestClusterMessage(final Calendar nodeTimestamp, final NodeData nodeData,
                                 final Calendar cacheTimestamp, final Map<String, String> cacheMapData,
                                 final Calendar registryTimestamp, final Map<Integer, ConfigData> configData) {
        this.nodeTimestamp = nodeTimestamp;
        this.cacheTimestamp = cacheTimestamp;
        this.registryTimestamp = registryTimestamp;
        this.nodeData = nodeData;
        this.cacheMapData = cacheMapData;
        this.configData = configData;
    }

    public Calendar getNodeTimestamp() {
        return nodeTimestamp;
    }

    public NodeData getNodeData() {
        return nodeData;
    }

    public Map<String, String> getCacheMapData() {
        return cacheMapData;
    }

    public Calendar getCacheTimestamp() {
        return cacheTimestamp;
    }

    public Calendar getRegistryTimestamp() {
        return registryTimestamp;
    }

    public void setRegistryTimestamp(Calendar registryTimestamp) {
        this.registryTimestamp = registryTimestamp;
    }

    public Map<Integer, ConfigData> getConfigData() {
        return configData;
    }

    public void setConfigData(Map<Integer, ConfigData> configData) {
        this.configData = configData;
    }

    public void setNodeTimestamp(Calendar nodeTimestamp) {
        this.nodeTimestamp = nodeTimestamp;
    }
}