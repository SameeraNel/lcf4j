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
package com.sdnelson.msc.research.lcf4j.http;

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
    static ExecutorService serverListener = Executors.newFixedThreadPool(1);

    public UptimeServer() {
        handler = new UptimeServerHandler();
    }

    public static void main(String[] args) throws Exception {
        new UptimeServer().startServer(8080);
    }
    public void startServer(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(2);
        EventLoopGroup workerGroup = new NioEventLoopGroup(10);
        Runnable listeningTask = () -> {
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
            ChannelFuture channelFuture = bootstrap.bind("localhost", port).sync();
            logger.info("Server started @ localhost =" + ":" + port);

                try {
                    logger.info("Server listening for client requests");
                    channelFuture.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            logger.info("Server waiting for client requests");
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
        };
        serverListener.execute(listeningTask);
    }
}
