package com.sdnelson.msc.research.lcf4j.util;


import com.sdnelson.msc.research.lcf4j.core.ClusterMessage;
import com.sdnelson.msc.research.lcf4j.core.NodeClusterMessage;
import com.sdnelson.msc.research.lcf4j.core.NodeRegistry;
import com.sdnelson.msc.research.lcf4j.core.RequestClusterMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class WebSocketFrameUtil {

    public static WebSocketFrame getNodeDataWebSocketFrame() throws IOException {
        return getWebSocketFrame(new NodeClusterMessage(NodeRegistry.getNodeDataList()));
    }

    public static WebSocketFrame requestClusterWebSocketFrame() throws IOException {
        return getWebSocketFrame(new RequestClusterMessage());
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
