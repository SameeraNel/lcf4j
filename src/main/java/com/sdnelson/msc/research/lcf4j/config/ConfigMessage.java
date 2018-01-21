package com.sdnelson.msc.research.lcf4j.config;

import com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.message.ClusterMessage;

import java.util.Calendar;

public class ConfigMessage implements ClusterMessage {

    private Calendar cacheTimestamp;
    private ConfigData configData;

    public ConfigMessage(Calendar cacheTimestamp, ConfigData configData) {
        this.cacheTimestamp = cacheTimestamp;
        this.configData = configData;
    }

    public Calendar getCacheTimestamp() {
        return cacheTimestamp;
    }

    public void setCacheTimestamp(Calendar cacheTimestamp) {
        this.cacheTimestamp = cacheTimestamp;
    }

    public ConfigData getConfigData() {
        return configData;
    }

    public void setConfigData(ConfigData configData) {
        this.configData = configData;
    }
}
