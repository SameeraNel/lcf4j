package com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.message;


import com.sdnelson.msc.research.lcf4j.core.NodeData;

import java.util.Calendar;

public class ConflictClusterMessage extends NodeClusterMessage  {

    public ConflictClusterMessage(final Calendar timestamp, final NodeData nodeData) {
        super(timestamp, nodeData);
    }
}
