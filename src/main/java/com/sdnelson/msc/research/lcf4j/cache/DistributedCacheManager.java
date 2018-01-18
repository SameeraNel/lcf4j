package com.sdnelson.msc.research.lcf4j.cache;

// For Cache related operations, add, update, delete to cacheRegistry
// Need to send cache updates on all operations, making it a distributed cache
public class DistributedCacheManager {

    public static String addToCache(String key, String value){
        return CacheRegistry.addToCache(key, value);
    }

    public static String updateCache(String key, String value){
        return CacheRegistry.updateCache(key, value);
    }

    public static String evictFromCache(String key){
        return CacheRegistry.evictFromCache(key);
    }
}
