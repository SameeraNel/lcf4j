package com.sdnelson.msc.research.lcf4j.cache;


import java.io.Serializable;
import java.util.Calendar;

public class CacheData implements Serializable {

    private Calendar timestamp;
    private String key;
    private String value;

    public CacheData(String key, String value) {
        this.timestamp = Calendar.getInstance();
        this.key = key;
        this.value = value;
    }

    public Calendar getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Calendar timestamp) {
        this.timestamp = timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "CacheData{" +
                "timestamp=" + timestamp +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
