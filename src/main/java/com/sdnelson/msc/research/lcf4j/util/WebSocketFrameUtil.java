package com.sdnelson.msc.research.lcf4j.util;


import com.sdnelson.msc.research.lcf4j.cache.CacheRegistry;
import com.sdnelson.msc.research.lcf4j.nodemgmt.NodeRegistry;
import com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.message.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class WebSocketFrameUtil {

    //Contains node data of sender
    public static WebSocketFrame getNodeDataWebSocketFrame() throws IOException {
        return getWebSocketFrame(
                new NodeClusterMessage(NodeRegistry.getTimestamp(),NodeRegistry.getServerNodeData()));
    }

    //Contains node data of sender
    public static WebSocketFrame getRequestClusterWebSocketFrame() throws IOException {
        return getWebSocketFrame(
                new RequestClusterMessage(NodeRegistry.getTimestamp(), CacheRegistry.getRegistryTimestamp(),
                        NodeRegistry.getServerNodeData(), CacheRegistry.getCacheMap()));
    }

    //Contains node data and the full cache data of sender
    public static WebSocketFrame getResponseClusterWebSocketFrame() throws IOException {
        return getWebSocketFrame(
                new ResponseClusterMessage(NodeRegistry.getTimestamp(), CacheRegistry.getRegistryTimestamp(),
                NodeRegistry.getServerNodeData(), CacheRegistry.getCacheMap()));
    }
    // Name conflict found
    public static WebSocketFrame getConflictClusterWebSocketFrame() throws IOException {
        return getWebSocketFrame(new ConflictClusterMessage(NodeRegistry.getServerNodeData()));
    }

    private static WebSocketFrame getWebSocketFrame(ClusterMessage clusterMessage) throws IOException {
        ByteBuf buf = Unpooled.buffer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(clusterMessage);
        oos.flush();
        buf.writeBytes(baos.toByteArray());
        return new BinaryWebSocketFrame(buf);
    }
}
