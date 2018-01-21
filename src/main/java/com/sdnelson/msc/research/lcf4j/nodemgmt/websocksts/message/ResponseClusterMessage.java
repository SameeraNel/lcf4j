package com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.message;


import com.sdnelson.msc.research.lcf4j.config.ConfigData;
import com.sdnelson.msc.research.lcf4j.core.NodeData;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ResponseClusterMessage extends RequestClusterMessage  {
    public ResponseClusterMessage(
            Calendar timestamp, NodeData nodeData,
            Calendar cacheTimestamp, Map<String, String> cacheMap,
            final Calendar registryTimestamp, final Map<Integer, ConfigData> configDataMap) {
        super(timestamp, nodeData, cacheTimestamp, cacheMap, registryTimestamp, configDataMap);
    }
}
