package com.sdnelson.msc.research.lcf4j.cache;

import com.sdnelson.msc.research.lcf4j.Lcf4jCluster;
import com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.client.WebSocketClient;
import org.apache.log4j.Logger;

import java.util.List;

// For Cache related operations, add, update, delete to cacheRegistry
// Need to send cache updates on all operations, making it a distributed cache
public class DistributedCacheManager {

    final static org.apache.log4j.Logger logger = Logger.getLogger(DistributedCacheManager.class);

    public static void addToCache(String key, String value){
        CacheRegistry.addToCache(key, value);
        final List<WebSocketClient> clientList = Lcf4jCluster.getClientList();
        if(clientList == null || clientList.isEmpty()){
            logger.info("No Client Nodes Found to send cache updates.");
            return;
        }
        for (WebSocketClient webSocketClient : clientList){
            webSocketClient.sendCacheUpdateMessage(new CacheData(key, value));
        }
        logger.info("Local cache updated and distributed successfully.");
    }

    public static void updateCache(String key, String value){
        addToCache(key, value);
    }

    public static void evictFromCache(String key){
        CacheRegistry.evictFromCache(key);
        final List<WebSocketClient> clientList = Lcf4jCluster.getClientList();
        if(clientList == null || clientList.isEmpty()){
            logger.info("No Client Nodes Found to send cache evictions.");
            return;
        }
        for (WebSocketClient webSocketClient : clientList){
            webSocketClient.sendCacheEvictMessage(new CacheData(key, null));
        }
        logger.info("Local cache data evicted and distributed successfully.");
    }
}
