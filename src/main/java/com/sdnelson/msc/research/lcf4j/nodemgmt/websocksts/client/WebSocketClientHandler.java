package com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.client;

import com.sdnelson.msc.research.lcf4j.cache.CacheData;
import com.sdnelson.msc.research.lcf4j.cache.CacheManager;
import com.sdnelson.msc.research.lcf4j.config.ConfigManager;
import com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.message.ConflictClusterMessage;
import com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.message.NodeClusterMessage;
import com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.message.ResponseClusterMessage;
import com.sdnelson.msc.research.lcf4j.nodemgmt.ClusterManager;
import com.sdnelson.msc.research.lcf4j.util.WebSocketFrameUtil;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {

    final static Logger logger = org.apache.log4j.Logger.getLogger(WebSocketClientHandler.class);

    private final WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;
    private String clientHostName;
    private static CacheData cacheData;

    public WebSocketClientHandler(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        logger.info("{" + ctx.channel() + "} Registered.");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        logger.info("{" + ctx.channel() + "} Unregistered.");
        if(clientHostName != null){
            ClusterManager.resolveUnRegistered(clientHostName);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("WebSocket Client disconnected!");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            try {
                handshaker.finishHandshake(ch, (FullHttpResponse) msg);
                logger.debug("Client connected.");
                handshakeFuture.setSuccess();
            } catch (WebSocketHandshakeException e) {
                logger.debug("Error occurred, Client failed to connect");
                handshakeFuture.setFailure(e);
            }
            return;
        }

        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            throw new IllegalStateException(
                    "Unexpected FullHttpResponse (getStatus=" + response.status() +
                            ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            logger.debug("Client received message: " + textFrame.text());
        } else if (frame instanceof BinaryWebSocketFrame) {
            ByteArrayInputStream baos = new ByteArrayInputStream(ByteBufUtil.getBytes(frame.content()));
            ObjectInputStream oos = new ObjectInputStream(baos);
            final Object readObject = oos.readObject();

            if(readObject instanceof NodeClusterMessage){
                handleNodeMessage(ctx, (NodeClusterMessage) readObject);
            } else if(readObject instanceof ResponseClusterMessage){
                handleResponseMessage(ctx, (ResponseClusterMessage) readObject);
            } else if(readObject instanceof ConflictClusterMessage){
                handleConflictMessage(ctx, (ConflictClusterMessage) readObject);
            //    ctx.fireChannelUnregistered();
            }
        } else if (frame instanceof PongWebSocketFrame) {
            logger.debug("WebSocket Client received pong");
        } else if (frame instanceof CloseWebSocketFrame) {
            logger.debug("WebSocket Client received closing");
            ch.close();
        }
    }

    private void handleConflictMessage(ChannelHandlerContext ctx, ConflictClusterMessage readObject) {
        logger.debug("Cluster conflict data message received from server [" + ctx.channel().remoteAddress() + "]");
        ConflictClusterMessage conflictClusterMessage = readObject;
        clientHostName = conflictClusterMessage.getNodeData().getNodeName();
        ClusterManager.resolveConflictDataMessage(conflictClusterMessage);
    }

    private void handleResponseMessage(ChannelHandlerContext ctx, ResponseClusterMessage readObject) {
        logger.debug("Cluster response data message received from server [" + ctx.channel().remoteAddress() + "]");
        ResponseClusterMessage responseClusterMessage = readObject;
        clientHostName = responseClusterMessage.getNodeData().getNodeName();
        ClusterManager.resolveResponseDataMessage(responseClusterMessage);
        CacheManager.resolveResponseCacheMessage(responseClusterMessage);
        ConfigManager.resolveResponseConfigMessage(responseClusterMessage);
    }

    private void handleNodeMessage(ChannelHandlerContext ctx, NodeClusterMessage readObject) {
        logger.debug("Cluster node data message received from server [" + ctx.channel().remoteAddress() + "]");
        NodeClusterMessage nodeClusterMessage = readObject;
        ClusterManager.resolveNodeDataMessage(nodeClusterMessage);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        ctx.writeAndFlush(WebSocketFrameUtil.getUpdateCacheWebSocketFrame(cacheData));
    }
}
