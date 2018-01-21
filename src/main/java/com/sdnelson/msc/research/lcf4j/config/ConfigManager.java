package com.sdnelson.msc.research.lcf4j.config;

import com.sdnelson.msc.research.lcf4j.core.NodeStatus;
import com.sdnelson.msc.research.lcf4j.nodemgmt.NodeRegistry;
import com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.message.ResponseClusterMessage;
import org.apache.log4j.Logger;

//Resolve config
public class ConfigManager {

    final static Logger logger = org.apache.log4j.Logger.getLogger(ConfigManager.class);

    public static void resolveResponseConfigMessage(ResponseClusterMessage responseClusterMessage) {
        if(ConfigRegistry.getConfigSize() == 0){
            logger.info("Config sync received : " + responseClusterMessage.getConfigData().toString());
            ConfigRegistry.updateInitConfig(responseClusterMessage.getConfigData());
            logger.info("Local Config updated.");
            NodeRegistry.markNodeStatus(NodeRegistry.getServerNodeData().getNodeName(), NodeStatus.ONLINE);
            logger.info("Node is set to ACTIVE.");
        }
    }

    public static void resolveConfigMessage(ConfigMessage configMessage) {
        logger.info("Config sync received : " + configMessage.getConfigData().toString());
        ConfigRegistry.addToConfig(configMessage.getConfigData());
        logger.info("Config Registry updated.");
    }
}
