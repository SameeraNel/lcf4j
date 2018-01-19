package com.sdnelson.msc.research.lcf4j.cache;

import java.util.Calendar;

public class EvictCacheMessage extends CacheMessage {

    public EvictCacheMessage(Calendar cacheTimestamp, CacheData cacheData) {
        super(cacheTimestamp, cacheData);
    }
}
