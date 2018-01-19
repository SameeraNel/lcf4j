package com.sdnelson.msc.research.lcf4j.cache;


import com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.message.ClusterMessage;

import java.io.Serializable;
import java.util.Calendar;

public class CacheMessage implements ClusterMessage {

    private Calendar cacheTimestamp;
    private CacheData cacheData;

    public CacheMessage(Calendar cacheTimestamp, CacheData cacheData) {
        this.cacheTimestamp = cacheTimestamp;
        this.cacheData = cacheData;
    }

    public CacheData getCacheData() {
        return cacheData;
    }

    public void setCacheData(CacheData cacheData) {
        this.cacheData = cacheData;
    }

    public Calendar getCacheTimestamp() {
        return cacheTimestamp;
    }

    public void setCacheTimestamp(Calendar cacheTimestamp) {
        this.cacheTimestamp = cacheTimestamp;
    }
}
