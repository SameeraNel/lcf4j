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
package com.sdnelson.msc.research.lcf4j;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class UptimeServer {

    final static Logger logger = Logger.getLogger(UptimeServer.class);
    private UptimeServerHandler handler;

    public UptimeServer() {
        handler = new UptimeServerHandler();
    }

    public void startServer(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(2);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        final Runnable runnableServer = RunnableServer(port, bossGroup, workerGroup);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(runnableServer);
    }

    private Runnable RunnableServer(int port, EventLoopGroup bossGroup, EventLoopGroup workerGroup) {
        Runnable runnableTask = () -> {
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup)
                         .channel(NioServerSocketChannel.class)
                         .handler(new LoggingHandler(LogLevel.INFO))
                         .childHandler(new ChannelInitializer<SocketChannel>() {

                             @Override public void initChannel(SocketChannel ch) {
                                 ch.pipeline().addLast(handler);
                             }
                         });

                // Bind and start to accept incoming connections.
                ChannelFuture channelFuture = bootstrap.bind(port).sync();
                logger.info("Server started @ localhost" + ":" + port);
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        };
        return runnableTask;
    }
}
