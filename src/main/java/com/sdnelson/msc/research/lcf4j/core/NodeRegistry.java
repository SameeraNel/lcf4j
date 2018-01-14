package com.sdnelson.msc.research.lcf4j.core;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class NodeRegistry {

    final static org.apache.log4j.Logger logger = Logger.getLogger(NodeRegistry.class);

    private static Calendar timestamp = Calendar.getInstance();

    private static ConcurrentHashMap<String, NodeData> nodeDataMap = new ConcurrentHashMap();

    public static NodeData addActiveNode(NodeData nodeData){
        nodeDataMap.put(nodeData.getNodeName(), nodeData);
        logger.info("[Node Added : " + nodeData.getNodeName() + "]");
        registryStats();
        return nodeData;
    }

    public static boolean markNodeStatus(String nodeKey, NodeStatus nodeStatus){
        final NodeData nodeData = getNodeData(nodeKey);
        if(nodeData == null){
            return false;
        }
        nodeData.setStatus(nodeStatus);
        nodeData.setLastUpdated(Calendar.getInstance());
        registryStats();
        return true;
    }

    public static boolean contains(String nodeKey){
        return nodeDataMap.containsKey(nodeKey);
    }

    public static NodeData getNodeData(String key){
        return nodeDataMap.get(key);
    }

    public static NodeStatus getNodeStatus(String key){
        final NodeData nodeData = nodeDataMap.get(key);
        if(nodeData == null){
            return NodeStatus.NOT_FOUND;
        }
        return nodeData.getStatus();
    }

    public static List<NodeData> getNodeDataList(){
        final ArrayList<NodeData> dataList = new ArrayList<>();
        dataList.addAll(nodeDataMap.values());
        registryStats();
        return dataList;
    }

    public static ConcurrentHashMap.KeySetView<String, NodeData> getNodeKeyList(){
        return nodeDataMap.keySet();
    }

    public static int getNodeCount(){
        return nodeDataMap.size();
    }

    private static void registryStats() {
        timestamp = Calendar.getInstance();
        StringBuilder nodeStat = new StringBuilder();
        nodeStat.append("[Node Count : " + NodeRegistry.getNodeCount() + "]");
        nodeStat.append("[");
        nodeDataMap.forEach((k, v) -> nodeStat.append( " {" + k + " : " + v.getStatus() + "} "));
        nodeStat.append("]");
        logger.info(nodeStat.toString());
    }

    public static Calendar getTimestamp() {
        return timestamp;
    }
}
