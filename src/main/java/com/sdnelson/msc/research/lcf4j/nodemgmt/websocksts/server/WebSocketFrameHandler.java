package com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.server;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.concurrent.ConcurrentHashMap;

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
            ByteArrayInputStream baos = new ByteArrayInputStream(ByteBufUtil.getBytes(frame.content()));
            ObjectInputStream oos = new ObjectInputStream(baos);
            final Object readObject = oos.readObject();
            if(readObject instanceof RequestClusterMessage){
                logger.info("Cluster node request message received from Client [" + ctx.channel().id().toString()
                        + " {" +  ctx.channel().remoteAddress().toString().replace("/", "") + "} ]" );
                RequestClusterMessage requestClusterMessage = (RequestClusterMessage) readObject;
                clientNodeMap.put(ctx.channel().id().toString(), requestClusterMessage.getNodeData().getNodeName());
                if(ClusterManager.resolveRequestDataMessage(requestClusterMessage)){
                    ctx.writeAndFlush(WebSocketFrameUtil.getResponseClusterWebSocketFrame());
                } else {
                    ctx.writeAndFlush(WebSocketFrameUtil.getConflictClusterWebSocketFrame());
                }
            } else if(readObject instanceof NodeClusterMessage){
                logger.info("Cluster node data message received from server [" + ctx.channel().remoteAddress() + "]");
                NodeClusterMessage nodeClusterMessage = (NodeClusterMessage) readObject;
                ClusterManager.resolveNodeDataMessage(nodeClusterMessage);
            } else {
                String message = "unsupported message type: " + oos.readObject().getClass();
                throw new UnsupportedOperationException(message);
            }

        }else {
            String message = "unsupported frame type: " + frame.getClass().getName();
            throw new UnsupportedOperationException(message);
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        logger.info("Client [" + ctx.channel().id().toString() +
                " {" +  ctx.channel().remoteAddress().toString().replace("/", "") + "} ] is Registered.");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        logger.info("Client [" + ctx.channel().id().toString() +
                " {" +  ctx.channel().remoteAddress().toString().replace("/", "") + "} ] is OFFLINE.");
        final String clientNodeName = clientNodeMap.get(ctx.channel().id().toString());
        ClusterManager.resolveUnRegistered(clientNodeName);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.info("Client [" + ctx.channel().id().toString()
                + " {" +  ctx.channel().remoteAddress().toString().replace("/", "") + "} ] is ONLINE.");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        logger.info("Client [" + ctx.channel().id().toString() +
                " {" +  ctx.channel().remoteAddress().toString().replace("/", "") + "} ] is INACTIVE.");
    }
}
