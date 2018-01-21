package com.sdnelson.msc.research.lcf4j.config;


import com.sdnelson.msc.research.lcf4j.util.ClusterConfig;
import org.apache.log4j.Logger;
import org.apache.log4j.lf5.util.StreamUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigRegistry {

    final static org.apache.log4j.Logger logger = Logger.getLogger(ConfigRegistry.class);

    private static Calendar registryTimestamp = Calendar.getInstance();

    private static ConcurrentHashMap<Integer, ConfigData> configMap = new ConcurrentHashMap();

    public static ConfigData addToLocalConfig(final ConfigData configData){
        int version = configMap.size() + 1;
        logger.info("Registry version [" + version +"] set with : [" + configData.toString() + "]");
        configData.setConfigVersion(version);
        updateTimestamp();
        configMap.put(version, configData);
        return configData;
    }

    public static ConfigData addToConfig(final ConfigData configData){
        int version = configData.getConfigVersion();
        logger.info("Registry version [" + version +"] set with : [" + configData.toString() + "]");
        configData.setConfigVersion(version);
        updateTimestamp();
        return configMap.put(version, configData);
    }

    public static Map<Integer, ConfigData> getServerConfigData(){
        final HashMap<Integer, ConfigData> configDataMap = new HashMap<>();
        configMap.values().stream().filter(
                configData ->
                        ClusterConfig.getNodeServerName().equals(configData.getNodeName())).forEach(
                configData -> {
            configDataMap.put(configData.getConfigVersion(), configData);
        });
        return configDataMap;
    }

    public static Calendar getRegistryTimestamp() {
        return registryTimestamp;
    }

    public static int getConfigSize() {
        return configMap.size();
    }

    public static void updateInitConfig(final Map<Integer, ConfigData> configDataMap) {
        updateTimestamp();
        configMap.putAll(configDataMap);
    }

    private static void updateTimestamp() {
        registryTimestamp = Calendar.getInstance();
        logger.info("[ Config Registry size :" + configMap.size() + "[ " + configMap.toString() + " ]");
        logger.info("Config updated @ [ " + registryTimestamp.getTime() + " ]");
    }

    public static HashMap<Integer, ConfigData> getAllConfigData() {
        logger.info("[Config Size : " + configMap.size() + "][" + configMap.toString() + "]");
        return new HashMap<>(configMap);
    }

    public static ConfigData getConfigForVersion(int version) {
        return configMap.get(version);
    }
}
