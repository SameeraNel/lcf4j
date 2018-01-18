package com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.message;


import com.sdnelson.msc.research.lcf4j.core.NodeData;

import java.util.Calendar;
import java.util.HashMap;

public class ResponseClusterMessage extends RequestClusterMessage  {
    public ResponseClusterMessage(Calendar timestamp, Calendar registryTimestamp, NodeData nodeData, HashMap<String, String> cacheMap) {
        super(timestamp, registryTimestamp, nodeData, cacheMap);
    }
}
