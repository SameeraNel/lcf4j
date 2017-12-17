package com.sdnelson.msc.research.lcf4j.websocksts.core;

import java.util.Calendar;

/**
 * Created by SDN on 12/17/2017.
 */
public class NodeData {

    private String nodeHexaId;
    private String nodeName;
    private String remoteAddress;
    private Calendar registeredTime;
    private Calendar unregisteredTime;
    private boolean status;

    public NodeData(String nodeHexaId, String remoteAddress) {
        this.nodeHexaId = nodeHexaId;
        this.remoteAddress = remoteAddress;
        this.registeredTime = Calendar.getInstance();
        this.status = true;
    }

    public String getNodeHexaId() {
        return nodeHexaId;
    }

    public void setNodeHexaId(String nodeHexaId) {
        this.nodeHexaId = nodeHexaId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public Calendar getRegisteredTime() {
        return registeredTime;
    }

    public void setRegisteredTime(Calendar registeredTime) {
        this.registeredTime = registeredTime;
    }

    public Calendar getUnregisteredTime() {
        return unregisteredTime;
    }

    public void setUnregisteredTime(Calendar unregisteredTime) {
        this.unregisteredTime = unregisteredTime;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "NodeData{" +
                "remoteAddress='" + remoteAddress + '\'' +
                ", registeredTime=" + registeredTime.getTime() +
                '}';
    }
}
