package com.sdnelson.msc.research.lcf4j.cache;

import com.sdnelson.msc.research.lcf4j.Lcf4jCluster;
import com.sdnelson.msc.research.lcf4j.config.ConfigData;
import com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.client.WebSocketClient;
import com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.client.WebSocketClientHandler;
import com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.server.WebSocketFrameHandler;
import com.sdnelson.msc.research.lcf4j.util.ClusterConfig;
import com.sdnelson.msc.research.lcf4j.util.WebSocketFrameUtil;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

// For Cache related operations, add, update, delete to cacheRegistry
// Need to send cache updates on all operations, making it a distributed cache
public class DistributedCacheManager {

    final static org.apache.log4j.Logger logger = Logger.getLogger(DistributedCacheManager.class);

    public static void addToCache(String key, String value){
        CacheRegistry.addToCache(key, value);
        sendCacheUpdateMessage(new CacheData(key, value));
//        final List<WebSocketClient> clientList = Lcf4jCluster.getClientList();
//        if(clientList == null || clientList.isEmpty()){
//            logger.info("No Client Nodes Found to send cache updates.");
//            return;
//        }
//        for (WebSocketClient webSocketClient : clientList){
//            webSocketClient.sendCacheUpdateMessage(new CacheData(key, value));
//        }
        logger.info("Local cache updated and distributed successfully.");
    }

    public static void updateCache(String key, String value){
        addToCache(key, value);
    }

    public static void evictFromCache(String key){
        CacheRegistry.evictFromCache(key);
        sendCacheEvictMessage(new CacheData(key, null));
//        final List<WebSocketClient> clientList = Lcf4jCluster.getClientList();
//        if(clientList == null || clientList.isEmpty()){
//            logger.info("No Client Nodes Found to send cache evictions.");
//            return;
//        }
//        for (WebSocketClient webSocketClient : clientList){
//            webSocketClient.sendCacheEvictMessage(new CacheData(key, null));
//        }
        logger.info("Local cache data evicted and distributed successfully.");
    }public static HashMap<String, String> getFullCache(){
        return CacheRegistry.getCacheMap();
    }

    public static String getCacheData(String key){
        return CacheRegistry.getCacheData(key);
    }


    public static int getCacheSize() {
        return CacheRegistry.getCacheSize();
    }

    public static void sendCacheUpdateMessage(final CacheData cacheData){
        final ChannelGroup recipients = WebSocketFrameHandler.getRecipients();
        final ChannelGroup serverChannelGroup = WebSocketClientHandler.getServerChannelGroup();
        try {
            if(serverChannelGroup == null || serverChannelGroup.isEmpty()){
                logger.info("Skipping cache update sending as there are no connected server nodes.");
            } else {
                for (Channel serverChannel : serverChannelGroup){
                    serverChannel.writeAndFlush(WebSocketFrameUtil.getUpdateCacheWebSocketFrame(cacheData));
                }
            }

            if(recipients == null || recipients.isEmpty()){
                logger.info("Skipping cache update sending as there are no connected client nodes.");
                return;
            } else {
                for (Channel channel : recipients) {
                    channel.writeAndFlush(WebSocketFrameUtil.getUpdateCacheWebSocketFrame(cacheData));
                    logger.info("Cache updates sent from [" + ClusterConfig.getNodeServerName() + "].");
                }
            }
        } catch (IOException e) {
            logger.error("Error occurred while sending the message.");
        }
    }

    public static void sendCacheEvictMessage(final CacheData cacheData){
        final ChannelGroup recipients = WebSocketFrameHandler.getRecipients();
        final ChannelGroup serverChannelGroup = WebSocketClientHandler.getServerChannelGroup();

        try {
            if(serverChannelGroup == null || serverChannelGroup.isEmpty()){
                logger.info("Skipping cache evict sending as there are no connected server nodes.");
            } else {
                for (Channel serverChannel : serverChannelGroup){
                    serverChannel.writeAndFlush(WebSocketFrameUtil.getEvictCacheWebSocketFrame(cacheData));
                }
            }

            if(recipients == null || recipients.isEmpty()){
                logger.info("Skipping cache evict sending as there are no connected client nodes.");
            }
            for(Channel channel : recipients){
                channel.writeAndFlush(WebSocketFrameUtil.getEvictCacheWebSocketFrame(cacheData));
            }
            logger.info("Cache evict sent from [" + ClusterConfig.getNodeServerName() + "].");
        } catch (IOException e) {
            logger.error("Error occured while sending the message.");
        }
    }
}
