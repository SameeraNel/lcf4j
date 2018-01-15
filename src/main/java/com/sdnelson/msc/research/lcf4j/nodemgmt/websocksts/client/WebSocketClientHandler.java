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
//The MIT License
//
//Copyright (c) 2009 Carl Bystršm
//
//Permission is hereby granted, free of charge, to any person obtaining a copy
//of this software and associated documentation files (the "Software"), to deal
//in the Software without restriction, including without limitation the rights
//to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//copies of the Software, and to permit persons to whom the Software is
//furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice shall be included in
//all copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//THE SOFTWARE.

package com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.client;

import com.sdnelson.msc.research.lcf4j.core.ConflictClusterMessage;
import com.sdnelson.msc.research.lcf4j.core.NodeClusterMessage;
import com.sdnelson.msc.research.lcf4j.core.RequestClusterMessage;
import com.sdnelson.msc.research.lcf4j.core.ResponseClusterMessage;
import com.sdnelson.msc.research.lcf4j.nodemgmt.core.ClusterManager;
import com.sdnelson.msc.research.lcf4j.util.WebSocketFrameUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.concurrent.TimeUnit;

public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {

    final static Logger logger = org.apache.log4j.Logger.getLogger(WebSocketClientHandler.class);

    private final WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;
    private String clientHostName;

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
                logger.info("WebSocket Client connected!");
                handshakeFuture.setSuccess();
            } catch (WebSocketHandshakeException e) {
                logger.info("WebSocket Client failed to connect");
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
            logger.info("Client received message: " + textFrame.text());
        } else if (frame instanceof BinaryWebSocketFrame) {
            ByteArrayInputStream baos = new ByteArrayInputStream(ByteBufUtil.getBytes(frame.content()));
            ObjectInputStream oos = new ObjectInputStream(baos);
            final Object readObject = oos.readObject();

            if(readObject instanceof NodeClusterMessage){
                logger.info("Cluster node data message received from server [" + ctx.channel().remoteAddress() + "]");
                NodeClusterMessage nodeClusterMessage = (NodeClusterMessage) readObject;
                ClusterManager.resolveNodeDataMessage(nodeClusterMessage);
            } else if(readObject instanceof ResponseClusterMessage){
                logger.info("Cluster response data message received from server [" + ctx.channel().remoteAddress() + "]");
                ResponseClusterMessage responseClusterMessage = (ResponseClusterMessage) readObject;
                clientHostName = responseClusterMessage.getNodeData().getNodeName();
                ClusterManager.resolveResponseDataMessage(responseClusterMessage);
            } else if(readObject instanceof ConflictClusterMessage){
                logger.info("Cluster conflict data message received from server [" + ctx.channel().remoteAddress() + "]");
                ConflictClusterMessage conflictClusterMessage = (ConflictClusterMessage) readObject;
                clientHostName = conflictClusterMessage.getNodeData().getNodeName();
                ClusterManager.resolveConflictDataMessage(conflictClusterMessage);
                ctx.fireChannelUnregistered();
            }
        } else if (frame instanceof PongWebSocketFrame) {
            logger.info("WebSocket Client received pong");
        } else if (frame instanceof CloseWebSocketFrame) {
            logger.info("WebSocket Client received closing");
            ch.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }
        ctx.close();
    }
}
