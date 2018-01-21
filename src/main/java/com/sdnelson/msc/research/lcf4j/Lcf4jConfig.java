package com.sdnelson.msc.research.lcf4j;


import com.sdnelson.msc.research.lcf4j.config.ConfigData;
import com.sdnelson.msc.research.lcf4j.config.DistributedConfigManager;

import java.util.Map;

public class Lcf4jConfig {

    public void addNewConfigVersion(Map<String, String> dataMap){
        DistributedConfigManager.addNewConfigVersion(dataMap);
    }

    public Map<Integer, ConfigData> getAllConfig(){
        return DistributedConfigManager.getAllConfig();
    }

    public ConfigData getConfigForVersion(int version){
        return DistributedConfigManager.getConfigForVersion(version);
    }

    public int getConfigSize(){
        return DistributedConfigManager.getConfigSize();
    }

}