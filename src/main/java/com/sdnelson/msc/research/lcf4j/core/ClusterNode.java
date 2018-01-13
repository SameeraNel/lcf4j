package com.sdnelson.msc.research.lcf4j.core;


public class ClusterNode {

    private String host;

    private int port;

    public ClusterNode(final String host, final int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
