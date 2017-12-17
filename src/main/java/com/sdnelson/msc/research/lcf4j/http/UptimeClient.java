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
package com.sdnelson.msc.research.lcf4j.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Connects to a server periodically to measure and print the uptime of the
 * server.  This example demonstrates how to implement reliable reconnection
 * mechanism in Netty.
 */
public final class UptimeClient {

    final static Logger logger = Logger.getLogger(UptimeClient.class);
    static final int RECONNECT_DELAY = Integer.parseInt(System.getProperty("reconnectDelay", "5"));
    private static final int READ_TIMEOUT = Integer.parseInt(System.getProperty("readTimeout", "3"));
    private static UptimeClientHandler handler;
    private static Bootstrap bootstrap;
    private static EventLoopGroup group;
    private ExecutorService clientExecutor;
    private String host;
    private int port;

    public UptimeClient(int nodeCount) {
        bootstrap = new Bootstrap();
        handler = new UptimeClientHandler();
        group = new NioEventLoopGroup();
        clientExecutor = Executors.newFixedThreadPool(nodeCount);
    }

    public void startClient(final String host, final int port) throws Exception {
        clientExecutor.execute(RunnableClient(host, port));
    }

    private static Runnable RunnableClient(String host, int port) {
        logger.debug("Client node requested for " + host + ":" + port);
        handler.setHost(host);
        handler.setPort(port);
        bootstrap.group(group).channel(NioSocketChannel.class).remoteAddress(host, port).handler(new ChannelInitializer<SocketChannel>() {
            @Override protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new IdleStateHandler(READ_TIMEOUT, 0, 0), handler);
            }
        });

        Runnable runnableTask = () -> {
            try {
                bootstrap.connect();
                logger.info("Client started @ " + host + ":" + port);
            } finally {
                group.shutdownGracefully();
            }
        };
        return runnableTask;
    }

    static void connect() {
        bootstrap.connect().addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                synchronized (handler.errorTime ) {
                    if (future.cause() != null) {
                        handler.println("Failed to connect to : "  +handler.getHost() + ':' + handler.getPort());
                    }
                }
            }
        });
    }
}
