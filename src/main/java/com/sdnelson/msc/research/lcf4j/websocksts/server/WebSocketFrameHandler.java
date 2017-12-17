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
package com.sdnelson.msc.research.lcf4j.websocksts.server;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import com.sdnelson.msc.research.lcf4j.websocksts.core.NodeData;
import com.sdnelson.msc.research.lcf4j.websocksts.core.NodeRegistry;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.apache.log4j.Logger;

/**
 * Echoes uppercase content of text frames.
 */
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    final static Logger logger = org.apache.log4j.Logger.getLogger(WebSocketFrameHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        // ping and pong frames already handled

        if (frame instanceof TextWebSocketFrame) {
            // Send the uppercase string back.
            String request = ((TextWebSocketFrame) frame).text();

            logger.info("{" + ctx.channel() + "} received {" +  request + "}");
            ctx.channel().writeAndFlush(new TextWebSocketFrame(request.toUpperCase(Locale.US)));
        } else {
            String message = "unsupported frame type: " + frame.getClass().getName();
            throw new UnsupportedOperationException(message);
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        NodeRegistry.addNode(new NodeData(ctx.channel().id().toString(),
                        ctx.channel().remoteAddress().toString().replace("/", "")));
        NodeRegistry.addNode(new NodeData(ctx.channel().id().toString(),
                        ctx.channel().remoteAddress().toString().replace("/", "")));
        logger.info("[" + ctx.channel().id().toString() +
                " {" +  ctx.channel().remoteAddress().toString().replace("/", "") + "} ] is ONLINE.");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        NodeRegistry.removeActiveNodeData(ctx.channel().id().toString());
        logger.info("[ Node Count : " + NodeRegistry.getActiveNodeCount() + "] " +
                "[ Active Node List : " + NodeRegistry.getActiveNodeDataList() + "]");
        logger.info("[ Node Count : " + NodeRegistry.getActiveNodeCount() + "] [ Active Node List : " + NodeRegistry.getActiveNodeKeyList() + "]");
        logger.info("[" + ctx.channel().id().toString() +
                " {" +  ctx.channel().remoteAddress().toString().replace("/", "") + "} ] is OFFLINE.");
        logger.info("[ Node Count : " + NodeRegistry.getGlobalNodeCount() + "] [ Global Node List : " + NodeRegistry.getGlobalNodeKeyList() + "]");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        TextWebSocketFrame frame = new TextWebSocketFrame("Registered.");
        ctx.channel().writeAndFlush(frame);
        logger.info("[" + ctx.channel().id().toString() +
                " {" +  ctx.channel().remoteAddress().toString().replace("/", "") + "} ] is ACTIVE.");
        logger.info("[ Node Count : " + NodeRegistry.getActiveNodeCount() + "] " +
                "[ Active Node List : " + NodeRegistry.getActiveNodeDataList() + "]");
        logger.info("[ Node Count : " + NodeRegistry.getActiveNodeCount() + "] [ Active Node List : " + NodeRegistry.getActiveNodeKeyList() + "]");
        logger.info("[ Node Count : " + NodeRegistry.getGlobalNodeCount() + "] [ Global Node List : " + NodeRegistry.getGlobalNodeKeyList() + "]");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        logger.info("[" + ctx.channel().id().toString() +
                " {" +  ctx.channel().remoteAddress().toString().replace("/", "") + "} ] is INACTIVE.");
    }
}
