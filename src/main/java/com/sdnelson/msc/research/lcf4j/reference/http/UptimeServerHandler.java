/*
 * Copyright 2017 The Netty Project
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
package com.sdnelson.msc.research.lcf4j.reference.http;

import com.sdnelson.msc.research.lcf4j.core.NodeData;
import com.sdnelson.msc.research.lcf4j.core.NodeRegistry;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.log4j.Logger;

import java.util.Date;

@Sharable
public class UptimeServerHandler extends SimpleChannelInboundHandler<Object> {

    final static Logger logger = org.apache.log4j.Logger.getLogger(UptimeServerHandler.class);

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("{" + ctx.channel() + "} received {" +  msg + "}");
        ctx.channel().writeAndFlush("Time : " + new Date() + ":" +" Node Count : " + NodeRegistry.getActiveNodeCount());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ctx.channel().writeAndFlush("Time : " + new Date() + ":" +"  Node Count : " + NodeRegistry.getActiveNodeCount());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ctx.channel().writeAndFlush("Time : " + new Date() + ":" +" Node Count : " + NodeRegistry.getActiveNodeCount());

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
        ctx.channel().writeAndFlush("Time : " + new Date() + ":" +"Node Count : " + NodeRegistry.getActiveNodeCount());
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
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
