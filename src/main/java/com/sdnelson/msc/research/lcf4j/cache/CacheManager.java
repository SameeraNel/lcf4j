package com.sdnelson.msc.research.lcf4j.cache;


import com.sdnelson.msc.research.lcf4j.core.NodeStatus;
import com.sdnelson.msc.research.lcf4j.nodemgmt.NodeRegistry;
import com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.message.ResponseClusterMessage;
import org.apache.log4j.Logger;

//Cache Resolve
public class CacheManager {

    final static org.apache.log4j.Logger logger = Logger.getLogger(CacheManager.class);

    public static void resolveResponseCacheMessage(final ResponseClusterMessage responseClusterMessage) {
        if(CacheRegistry.getCacheSize() == 0){
            logger.info("Cache update found, Updating ...");
            CacheRegistry.updateFullCache(responseClusterMessage.getCacheMapData());
            NodeRegistry.markNodeStatus(NodeRegistry.getServerNodeData().getNodeName(), NodeStatus.ONLINE);
        }
    }
}
