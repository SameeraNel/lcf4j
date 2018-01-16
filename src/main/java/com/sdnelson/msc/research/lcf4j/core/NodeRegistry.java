package com.sdnelson.msc.research.lcf4j.core;

import com.sdnelson.msc.research.lcf4j.util.ClusterConfig;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class NodeRegistry {

    final static org.apache.log4j.Logger logger = Logger.getLogger(NodeRegistry.class);

    private static Calendar timestamp = Calendar.getInstance();

    private static ConcurrentHashMap<String, NodeData> nodeDataMap = new ConcurrentHashMap();

    public static NodeData addActiveNode(NodeData nodeData){
        nodeDataMap.put(nodeData.getNodeName(), nodeData);
        logger.info("[Node updated : " + nodeData.getNodeName() + "]");
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

    public static NodeData getNodeByRemoteHostName(String hostName){
        for (NodeData nodeData : nodeDataMap.values()){
            if(hostName.equals(nodeData.getHostName())){
                return nodeData;
            }
        }
        return null;
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

    public static NodeData refreshLastUpdated(final String key){
        final NodeData nodeData = nodeDataMap.get(key);
        nodeData.refreshLastUpdated();
        registryStats();
        return nodeData;
    }

    public static List<NodeData> getNodeDataList(){
        final ArrayList<NodeData> dataList = new ArrayList<>();
        dataList.addAll(nodeDataMap.values());
        return dataList;
    }

    public static NodeData getServerNodeData(){
        return nodeDataMap.get(ClusterConfig.getNodeServerName());
    }

    public static ConcurrentHashMap.KeySetView<String, NodeData> getNodeKeyList(){
        return nodeDataMap.keySet();
    }

    public static int getNodeCount(){
        return nodeDataMap.size();
    }

    public static int getActiveNodeCount(){
        final Collection<NodeData> values = nodeDataMap.values();
        int activeCount = 0;
        for (NodeData nodeData : values){
            if(NodeStatus.ACTIVE.equals(nodeData.getStatus())){
                activeCount++;
            }
        }
        return activeCount;
    }

    public static List<NodeData> getActiveNodeList(){
        final Collection<NodeData> values = nodeDataMap.values();
        List<NodeData> nodeDataList = new ArrayList<>();
        for (NodeData nodeData : values){
            if(NodeStatus.ACTIVE.equals(nodeData.getStatus())){
                nodeDataList.add(nodeData);
            }
        }
        return nodeDataList;
    }

    public static Map<String, NodeData> getActiveNodeMap(){
        final Collection<NodeData> values = nodeDataMap.values();
        Map<String, NodeData> nodeDataMap = new HashMap<>();
        for (NodeData nodeData : values){
            if(NodeStatus.ACTIVE.equals(nodeData.getStatus())){
                nodeDataMap.put(nodeData.getNodeName(), nodeData);
            }
        }
        return nodeDataMap;
    }

    private static void registryStats() {
        timestamp = Calendar.getInstance();
        StringBuilder nodeStat = new StringBuilder();
        nodeStat.append("[Node Count : " + NodeRegistry.getNodeCount() + "]");
        nodeStat.append("[");
        nodeDataMap.forEach((k, v) -> nodeStat.append( " {" + k + " : " + v.getStatus() +  " : " + v.getLastUpdated().getTime() + "} "));
        nodeStat.append("]");
        logger.info(nodeStat.toString());
    }

    public static Calendar getTimestamp() {
        return timestamp;
    }
}
