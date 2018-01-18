package com.sdnelson.msc.research.lcf4j.cache;


import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class CacheRegistry {

    final static org.apache.log4j.Logger logger = Logger.getLogger(CacheRegistry.class);

    private static Calendar registryTimestamp = Calendar.getInstance();

    private static ConcurrentHashMap<String, String> cacheMap = new ConcurrentHashMap();

    public static String addToCache(String key, String value){
        updateTimestamp();
        return cacheMap.put(key, value);
    }

    private static void updateTimestamp() {
        logger.info("Cache update @ " + registryTimestamp.getTime());
        registryTimestamp = Calendar.getInstance();
    }

    public static String updateCache(String key, String value){
        updateTimestamp();
        return addToCache(key, value);
    }

    public static String evictFromCache(String key){
        updateTimestamp();
        return cacheMap.remove(key);
    }

    public static HashMap<String, String> getCacheMap(){
        updateTimestamp();
        return new HashMap<>(cacheMap);
    }

    public static Calendar getRegistryTimestamp() {
        return registryTimestamp;
    }

    public static int getCacheSize() {
        return cacheMap.size();
    }

    public static void updateFullCache(HashMap<String, String> cacheMapData) {
        cacheMap.putAll(cacheMapData);
    }
}
