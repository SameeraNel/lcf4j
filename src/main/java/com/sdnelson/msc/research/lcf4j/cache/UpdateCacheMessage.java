package com.sdnelson.msc.research.lcf4j.cache;

import java.util.Calendar;

public class UpdateCacheMessage extends CacheMessage {

    public UpdateCacheMessage(Calendar cacheTimestamp, CacheData cacheData) {
        super(cacheTimestamp, cacheData);
    }
}
