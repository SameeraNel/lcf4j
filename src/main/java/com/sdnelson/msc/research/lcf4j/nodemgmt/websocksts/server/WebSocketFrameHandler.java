package com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.ConcurrentHashMap;

import com.sdnelson.msc.research.lcf4j.cache.CacheManager;
import com.sdnelson.msc.research.lcf4j.cache.EvictCacheMessage;
import com.sdnelson.msc.research.lcf4j.cache.UpdateCacheMessage;
import com.sdnelson.msc.research.lcf4j.config.ConfigManager;
import com.sdnelson.msc.research.lcf4j.config.ConfigMessage;
import com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.message.NodeClusterMessage;
import com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.message.RequestClusterMessage;
import com.sdnelson.msc.research.lcf4j.nodemgmt.ClusterManager;
import com.sdnelson.msc.research.lcf4j.util.ClusterConfig;
import com.sdnelson.msc.research.lcf4j.util.WebSocketFrameUtil;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.apache.log4j.Logger;


public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    final static Logger logger = org.apache.log4j.Logger.getLogger(WebSocketFrameHandler.class);
    final String nodeName = ClusterConfig.getNodeServerName();
    private static ConcurrentHashMap<String, String> clientNodeMap = new ConcurrentHashMap();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {

        if (frame instanceof BinaryWebSocketFrame) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(ByteBufUtil.getBytes(frame.content()));
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            final Object readObject = objectInputStream.readObject();
            if(readObject instanceof RequestClusterMessage){
                handleRequestCluster(ctx, (RequestClusterMessage) readObject);
            } else if(readObject instanceof NodeClusterMessage){
                handleNodeCluster(ctx, (NodeClusterMessage) readObject);
            } else if(readObject instanceof UpdateCacheMessage) {
                handleUpdateCache(ctx, (UpdateCacheMessage) readObject);
            } else if(readObject instanceof EvictCacheMessage) {
                handleEvictCache(ctx, (EvictCacheMessage) readObject);
            } else if(readObject instanceof EvictCacheMessage) {
                handleEvictCache(ctx, (EvictCacheMessage) readObject);
            } else if(readObject instanceof ConfigMessage) {
                handleConfigMessage(ctx, (ConfigMessage) readObject);
            } else {
                String message = "unsupported message type: " + objectInputStream.readObject().getClass();
                throw new UnsupportedOperationException(message);
            }

        }else {
            String message = "unsupported frame type: " + frame.getClass().getName();
            throw new UnsupportedOperationException(message);
        }
    }

    private void handleConfigMessage(ChannelHandlerContext ctx, ConfigMessage configMessage) {
        logger.debug("New config version message received from the server [" + ctx.channel().remoteAddress() + "]");
        ConfigManager.resolveConfigMessage(configMessage);
    }

    private void handleEvictCache(ChannelHandlerContext ctx, EvictCacheMessage evictCacheMessage) {
        logger.debug("Cache evict message received from the server [" + ctx.channel().remoteAddress() + "]");
        CacheManager.resolveCacheEvictMessage(evictCacheMessage);
    }

    private void handleRequestCluster(ChannelHandlerContext ctx, RequestClusterMessage requestClusterMessage) throws IOException {
        logger.debug("Cluster node request message received from the Client [" + ctx.channel().id().toString()
                + " {" +  ctx.channel().remoteAddress().toString().replace("/", "") + "} ]" );
        clientNodeMap.put(ctx.channel().id().toString(), requestClusterMessage.getNodeData().getNodeName());
        if(ClusterManager.resolveRequestNodeData(requestClusterMessage)){
            ctx.writeAndFlush(WebSocketFrameUtil.getResponseClusterWebSocketFrame());
        } else {
            ctx.writeAndFlush(WebSocketFrameUtil.getConflictClusterWebSocketFrame());
        }
    }

    private void handleNodeCluster(ChannelHandlerContext ctx, NodeClusterMessage nodeClusterMessage) throws IOException {
        logger.debug("Cluster node data message received from server [" + ctx.channel().remoteAddress() + "]");
        ClusterManager.resolveNodeDataMessage(nodeClusterMessage);
        ctx.writeAndFlush(WebSocketFrameUtil.getNodeDataWebSocketFrame());
    }

    private void handleUpdateCache(ChannelHandlerContext ctx, UpdateCacheMessage updateCacheMessage) {
        logger.debug("Cache update message received from server [" + ctx.channel().remoteAddress() + "]");
        CacheManager.resolveCacheUpdateMessage(updateCacheMessage);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        logger.debug("Client [" + ctx.channel().id().toString() +
                " {" +  ctx.channel().remoteAddress().toString().replace("/", "") + "} ] is Registered.");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        logger.debug("Client [" + ctx.channel().id().toString() +
                " {" +  ctx.channel().remoteAddress().toString().replace("/", "") + "} ] is OFFLINE.");
        final String clientNodeName = clientNodeMap.get(ctx.channel().id().toString());
        ClusterManager.resolveUnRegistered(clientNodeName);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.debug("Client [" + ctx.channel().id().toString()
                + " {" +  ctx.channel().remoteAddress().toString().replace("/", "") + "} ] is ONLINE.");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        logger.debug("Client [" + ctx.channel().id().toString() +
                " {" +  ctx.channel().remoteAddress().toString().replace("/", "") + "} ] is INACTIVE.");
    }
}
