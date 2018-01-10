package com.sdnelson.msc.research.lcf4j.core;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by SDN on 12/17/2017.
 */
public class NodeRegistry {

    private static ConcurrentHashMap<String, NodeData> globalNodeMap = new ConcurrentHashMap();

    private static ConcurrentHashMap<String, NodeData> activeNodeMap = new ConcurrentHashMap();

    public static NodeData addNode(NodeData nodeData){
        globalNodeMap.put(nodeData.getNodeHexaId(), nodeData);
        activeNodeMap.put(nodeData.getNodeHexaId(), nodeData);
        return nodeData;
    }

    public static boolean contains(NodeData nodeData){
        return activeNodeMap.containsKey(nodeData.getNodeHexaId());
    }

    public static NodeData getActiveNodeData(String key){
        return activeNodeMap.get(key);
    }

    public static Collection<NodeData> getActiveNodeDataList(){
        return activeNodeMap.values();
    }

    public static ConcurrentHashMap.KeySetView<String, NodeData> getActiveNodeKeyList(){
        return activeNodeMap.keySet();
    }

    public static NodeData getGlobalNodeData(String key){
        return globalNodeMap.get(key);
    }

    public static Collection<NodeData> getGlobalNodeDataList(){
        return globalNodeMap.values();
    }

    public static ConcurrentHashMap.KeySetView<String, NodeData> getGlobalNodeKeyList(){
        return globalNodeMap.keySet();
    }

    public static NodeData removeActiveNodeData(String key){
        return activeNodeMap.remove(key);
    }

    public static int getActiveNodeCount(){
        return activeNodeMap.size();
    }

    public static int getGlobalNodeCount(){
        return globalNodeMap.size();
    }


}
