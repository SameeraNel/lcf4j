package com.sdnelson.msc.research.lcf4j;

import com.sdnelson.msc.research.lcf4j.cache.DistributedCacheManager;

import java.util.HashMap;

public class Lcf4jCache {

    public void addToCache(final String key, final String value){
        DistributedCacheManager.addToCache(key, value);
    }

    public void updateCache(final String key, final String value){
        DistributedCacheManager.updateCache(key, value);
    }

    public void evictFromCache(final String key){
        DistributedCacheManager.evictFromCache(key);
    }

    public HashMap<String, String> getFullCache(){
        return DistributedCacheManager.getFullCache();
    }

    public String getCacheData(String key){
        return DistributedCacheManager.getCacheData(key);
    }

    public int getCacheSize() {
        return DistributedCacheManager.getCacheSize();
    }
}
