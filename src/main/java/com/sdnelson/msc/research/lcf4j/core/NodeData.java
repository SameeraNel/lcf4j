package com.sdnelson.msc.research.lcf4j.core;

import java.io.Serializable;
import java.util.Calendar;

public class NodeData implements Serializable {

    private String nodeName;
    private String hostName;
    private Calendar lastUpdated;
    private Calendar startTime;
    private NodeStatus status;

    public NodeData(String nodeName, String hostName) {
        this.nodeName = nodeName;
        this.hostName = hostName;
        this.startTime = Calendar.getInstance();
        this.lastUpdated = Calendar.getInstance();
        this.status = NodeStatus.ACTIVE;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Calendar getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Calendar lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public NodeStatus getStatus() {
        return status;
    }

    public void setStatus(NodeStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "NodeData{" +
                "nodeName='" + nodeName + '\'' +
                ", hostName='" + hostName + '\'' +
                ", lastUpdated=" + lastUpdated.getTime() +
                ", startTime=" + startTime.getTime() +
                ", status=" + status +
                '}';
    }
}
