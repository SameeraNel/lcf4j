package com.sdnelson.msc.research.lcf4j.config;


import com.sdnelson.msc.research.lcf4j.Lcf4jCluster;
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
import java.util.Map;

public class DistributedConfigManager {

    final static org.apache.log4j.Logger logger = Logger.getLogger(DistributedConfigManager.class);

    public static void addNewConfigVersion(Map<String, String> config){
        //Set version as 0 here, but in registry it will be check and set for correct value
        final ConfigData configData = new ConfigData(ClusterConfig.getNodeServerName(), 0, new HashMap<>(config));
        // Returned data will contained updated version
        final ConfigData localConfigData = ConfigRegistry.addToLocalConfig(configData);
        sendConfigUpdateMessage(localConfigData);
        logger.info("Local config registry updated and distributed to the cluster successfully.");
    }

    public static Map<Integer,ConfigData> getAllConfig() {
        return ConfigRegistry.getAllConfigData();
    }

    public static ConfigData getConfigForVersion(int version) {
        return ConfigRegistry.getConfigForVersion(version);
    }

    public static int getConfigSize() {
        return ConfigRegistry.getConfigSize();
    }

    private static void sendConfigUpdateMessage(final ConfigData configData){
        final ChannelGroup clientChannelGroup = WebSocketFrameHandler.getRecipients();
        final ChannelGroup serverChannelGroup = WebSocketClientHandler.getServerChannelGroup();
        try {
            if(serverChannelGroup == null || serverChannelGroup.isEmpty()){
                logger.info("Skipping config update sending as there are no connected server nodes.");
            } else {
                for (Channel serverChannel : serverChannelGroup){
                    serverChannel.writeAndFlush(WebSocketFrameUtil.getConfigWebSocketFrame(configData));
                    logger.info("Server config update [" + configData.toString() + "] sent from [" +  ClusterConfig.getNodeServerName() + "].");
                }
            }

            if(clientChannelGroup == null || clientChannelGroup.isEmpty()){
                logger.info("Skipping config update sending as there are no connected client nodes.");
            } else {
                for (Channel channel : clientChannelGroup) {
                    channel.writeAndFlush(WebSocketFrameUtil.getConfigWebSocketFrame(configData));
                    logger.info("CLient config update [" + configData.toString() + "] sent from [" + ClusterConfig.getNodeServerName() + "].");
                }
            }
        } catch (IOException e) {
            logger.error("Error occurred while sending the message.");
        }
    }
}
