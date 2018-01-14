/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.server;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

import com.sdnelson.msc.research.lcf4j.core.NodeClusterMessage;
import com.sdnelson.msc.research.lcf4j.core.NodeData;
import com.sdnelson.msc.research.lcf4j.core.NodeRegistry;
import com.sdnelson.msc.research.lcf4j.core.RequestClusterMessage;
import com.sdnelson.msc.research.lcf4j.nodemgmt.core.ClusterManager;
import com.sdnelson.msc.research.lcf4j.util.ClusterConfig;
import com.sdnelson.msc.research.lcf4j.util.WebSocketFrameUtil;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.apache.log4j.Logger;

/**
 * Echoes uppercase content of text frames.
 */
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    final static Logger logger = org.apache.log4j.Logger.getLogger(WebSocketFrameHandler.class);
    final String nodeName = ClusterConfig.getNodeServerName();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {

        if (frame instanceof BinaryWebSocketFrame) {
            ByteArrayInputStream baos = new ByteArrayInputStream(ByteBufUtil.getBytes(frame.content()));
            ObjectInputStream oos = new ObjectInputStream(baos);
            if( oos.readObject() instanceof RequestClusterMessage){
                logger.info("Cluster node request message received from Client [" + ctx.channel().id().toString()
                        + " {" +  ctx.channel().remoteAddress().toString().replace("/", "") + "} ]" );
                ctx.writeAndFlush(WebSocketFrameUtil.getNodeDataWebSocketFrame());

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
