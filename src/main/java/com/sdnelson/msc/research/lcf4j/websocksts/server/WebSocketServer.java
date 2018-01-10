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

import com.sdnelson.msc.research.lcf4j.websocksts.core.NodeData;
import com.sdnelson.msc.research.lcf4j.websocksts.core.NodeRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class WebSocketServer {

    final static org.apache.log4j.Logger logger = Logger.getLogger(WebSocketServer.class);
    static final boolean SSL = System.getProperty("ssl") != null;
    static final int PORT = Integer.parseInt(System.getProperty("port", SSL? "8443" : "8080"));

    public static void main(String[] args) throws Exception {
        logger.info("Application is Starting ...");
        final Properties properties = loadConfigFile();
        final String hostName = properties.getProperty("lcf4j.server.host");
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }
        logger.info("Server boot sequence initiated.");
        EventLoopGroup bossGroup = new NioEventLoopGroup(
                Integer.valueOf(properties.getProperty("server.master.threadpool.size")));
        EventLoopGroup workerGroup = new NioEventLoopGroup(Integer.valueOf(
                properties.getProperty("server.slave.threadpool.size")));
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .handler(new LoggingHandler(LogLevel.DEBUG))
             .childHandler(new WebSocketServerInitializer(sslCtx));

            logger.info("Server boot sequence initiated.");
            Channel ch = b.bind(PORT).sync().channel();
            logger.info("Server boot sequence Completed.");
            NodeRegistry.addNode(new NodeData(ch.id().toString(), hostName));
            logger.info("[ Node Count : " + NodeRegistry.getActiveNodeCount() + "] " +
                    "[ Active Node List : " + NodeRegistry.getActiveNodeDataList() + "]");
            logger.info("[ Node Count : " + NodeRegistry.getActiveNodeCount() + "] [ Active Node List : " + NodeRegistry.getActiveNodeKeyList() + "]");
            logger.info("[ Node Count : " + NodeRegistry.getGlobalNodeCount() + "] [ Global Node List : " + NodeRegistry.getGlobalNodeKeyList() + "]");

            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private static Properties loadConfigFile() {
        Properties prop = new Properties();
        InputStream input = null;

        try {

            String filename = "lcf4j.properties";
            input = WebSocketServer.class.getClassLoader().getResourceAsStream(filename);
            if(input==null){
                logger.error("Sorry, unable to find " + filename);
                return null;
            }

            prop.load(input);
            logger.info("Property file loaded successfully.");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally{
            if(input!=null){
                try {
                    input.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return prop;
    }
}
