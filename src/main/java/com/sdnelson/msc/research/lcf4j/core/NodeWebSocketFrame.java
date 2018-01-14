package com.sdnelson.msc.research.lcf4j.core;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.Calendar;


public class NodeWebSocketFrame extends WebSocketFrame {

    private Calendar timestamp;

    public NodeWebSocketFrame(ByteBuf binaryData) {
        super(binaryData);
        timestamp = Calendar.getInstance();
    }

    protected NodeWebSocketFrame(boolean finalFragment, int rsv, ByteBuf binaryData) {
        super(finalFragment, rsv, binaryData);
        timestamp = Calendar.getInstance();
    }

    @Override
    public WebSocketFrame replace(ByteBuf content) {
        return new NodeWebSocketFrame(content);
    }

    public Calendar getTimestamp() {
        return timestamp;
    }
}
