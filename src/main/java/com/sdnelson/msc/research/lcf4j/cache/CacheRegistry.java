package com.sdnelson.msc.research.lcf4j.cache;


import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheRegistry {

    final static org.apache.log4j.Logger logger = Logger.getLogger(CacheRegistry.class);

    private static Calendar registryTimestamp = Calendar.getInstance();

    private static ConcurrentHashMap<String, String> cacheMap = new ConcurrentHashMap();

    public static void addToCache(String key, String value){
        cacheMap.put(key, value);
        updateTimestamp();
    }

    private static void updateTimestamp() {
        registryTimestamp = Calendar.getInstance();
        logger.info("[ Cache Registry size :" + cacheMap.size() + "[ " + cacheMap.toString() + " ]");
        logger.info("Cache updated @ [ " + registryTimestamp.getTime() + " ]");
    }

    public static void updateCache(String key, String value){
        addToCache(key, value);
        updateTimestamp();
    }

    public static void evictFromCache(String key){
        cacheMap.remove(key);
        updateTimestamp();
    }

    public static HashMap<String, String> getCacheMap(){
        logger.info("[Cache Size : " + cacheMap.size() + "][" + cacheMap.toString() + "]");
        return new HashMap<>(cacheMap);
    }

    public static Calendar getRegistryTimestamp() {
        return registryTimestamp;
    }

    public static int getCacheSize() {
        return cacheMap.size();
    }

    public static void updateFullCache(Map<String, String> cacheMapData) {
        cacheMap.putAll(cacheMapData);
    }

    public static String getCacheData(String key) {
        return cacheMap.get(key);
    }
}
