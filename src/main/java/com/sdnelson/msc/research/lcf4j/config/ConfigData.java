package com.sdnelson.msc.research.lcf4j.config;


import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConfigData implements Serializable {

    private Calendar timestamp;
    private String nodeName;
    private Integer configVersion;
    private Map<String, String> configMap;

    public ConfigData(String nodeName, Integer configVersion, Map<String, String> configMap) {
        this.timestamp = Calendar.getInstance();
        this.nodeName = nodeName;
        this.configVersion = configVersion;
        this.configMap = configMap;
    }

    public Calendar getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Calendar timestamp) {
        this.timestamp = timestamp;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Integer getConfigVersion() {
        return configVersion;
    }

    public void setConfigVersion(Integer configVersion) {
        this.configVersion = configVersion;
    }

    public Map<String, String> getConfigMap() {
        return configMap;
    }

    public void setConfigMap(Map<String, String> configMap) {
        this.configMap = configMap;
    }

    @Override
    public String toString() {
        return "ConfigData{" +
                ", nodeName=" + nodeName +
                ", configMap=" + configMap +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConfigData)) return false;
        ConfigData that = (ConfigData) o;
        return Objects.equals(nodeName, that.nodeName) &&
                Objects.equals(configVersion, that.configVersion) &&
                Objects.equals(configMap, that.configMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeName, configVersion, configMap);
    }
}
