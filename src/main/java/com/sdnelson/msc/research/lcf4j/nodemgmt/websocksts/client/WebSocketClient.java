/*
 * Copyright 2014 The Netty Project
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
package com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.client;

import com.sdnelson.msc.research.lcf4j.core.NodeData;
import com.sdnelson.msc.research.lcf4j.core.NodeRegistry;
import com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.server.WebSocketServer;
import com.sdnelson.msc.research.lcf4j.websocksts.server.WebSocketServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URI;
import java.util.Properties;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.Properties;

/**
 * This is an example of a WebSocket client.
 * <p>
 * In order to run this example you need a compatible WebSocket server.
 * Therefore you can either start the WebSocket server from the examples
 * by running {@link com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.server.WebSocketServer}
 * or connect to an existing WebSocket server such as
 * <a href="http://www.websocket.org/echo.html">ws://echo.websocket.org</a>.
 * <p>
 * The client will attempt to connect to the URI passed to it as the first argument.
 * You don't have to specify any arguments if you want to connect to the example WebSocket server,
 * as this is the default.
 */
public final class WebSocketClient {

    final static org.apache.log4j.Logger logger = Logger.getLogger(WebSocketServer.class);
    final static org.apache.log4j.Logger logger = Logger.getLogger(WebSocketClient.class);
    static final String URL = System.getProperty("url", "ws://127.0.0.1:8080/websocket");

    public static void main(String[] args) throws Exception {
        new WebSocketClient().startClient();
    }
    public void startClient() throws Exception {
        final Properties properties = loadConfigFile();
        new WebSocketClient().runClient();
    }
    public  void runClient() throws Exception {
        logger.info("Client is starting ...");
        final Properties properties = loadConfigFile();
        URI uri = new URI(URL);
        String scheme = uri.getScheme() == null? "ws" : uri.getScheme();
        final String host = uri.getHost() == null? "127.0.0.1" : uri.getHost();
        final int port;
        if (uri.getPort() == -1) {
            if ("ws".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("wss".equalsIgnoreCase(scheme)) {
                port = 443;
            } else {
                port = -1;
            }
        } else {
            port = uri.getPort();
        }

        if (!"ws".equalsIgnoreCase(scheme) && !"wss".equalsIgnoreCase(scheme)) {
            System.err.println("Only WS(S) is supported.");
            return;
        }

        final boolean ssl = "wss".equalsIgnoreCase(scheme);
        final SslContext sslCtx;
        if (ssl) {
            sslCtx = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        EventLoopGroup group = new NioEventLoopGroup(Integer.valueOf(
                properties.getProperty("client.salve.threadpool.size")));
        logger.info(properties.getProperty("client.slave.threadpool.size"));
        EventLoopGroup group = new NioEventLoopGroup(Integer.parseInt(properties.getProperty("client.slave.threadpool.size")));
        ChannelFuture sync;
        try {
            // Connect with V13 (RFC 6455 aka HyBi-17). You can change it to V08 or V00.
            // If you change it to V00, ping is not supported and remember to change
            // HttpResponseDecoder to WebSocketHttpResponseDecoder in the pipeline.
            final WebSocketClientHandler handler =
                    new WebSocketClientHandler(
                            WebSocketClientHandshakerFactory.newHandshaker(
                                    uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()));

            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 protected void initChannel(SocketChannel ch) {
                     ChannelPipeline p = ch.pipeline();
                     if (sslCtx != null) {
                         p.addLast(sslCtx.newHandler(ch.alloc(), host, port));
                     }
                     p.addLast(
                             new HttpClientCodec(),
                             new HttpObjectAggregator(8192),
                             WebSocketClientCompressionHandler.INSTANCE,
                             handler);
                 }
             });
            logger.info("Client boot sequence initiated.");
            Channel ch = b.connect(uri.getHost(), port).sync().channel();
            logger.info("Client boot sequence completed.");

            handler.handshakeFuture().sync();
            sync = handler.handshakeFuture().sync();

            while (true) {
                WebSocketFrame frame = new TextWebSocketFrame(ch.localAddress().toString());
                ch.writeAndFlush(frame);
                Thread.sleep(1000);
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));


            while(true){
                String hashtext = "";
                Collection<NodeData> activeNodeDataList = NodeRegistry.getActiveNodeDataList();
                for (NodeData dataList : activeNodeDataList) {
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    byte[] messageDigest = md.digest(dataList.toString().getBytes());
                    BigInteger number = new BigInteger(1, messageDigest);
                    hashtext += number.toString(16);
                }
                ch.writeAndFlush(new TextWebSocketFrame("NodeData Hash : " + hashtext + ":" +" Node Count : " + NodeRegistry.getActiveNodeCount()));
                Thread.sleep(1000);
            }


//            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
//            while (true) {
//                String msg = console.readLine();
//                if (msg == null) {
//                    break;
//                } else if ("bye".equals(msg.toLowerCase())) {
//                    logger.info("Client disconnect initiated.");
//                    ch.writeAndFlush(new CloseWebSocketFrame());
//                    ch.closeFuture().sync();
//                    break;
//                } else if ("ping".equals(msg.toLowerCase())) {
//                    WebSocketFrame frame = new PingWebSocketFrame(Unpooled.wrappedBuffer(new byte[] { 8, 1, 8, 1 }));
//                    ch.writeAndFlush(frame);
//                } else {
//                    logger.info("Message Sent.");
//                    WebSocketFrame frame = new TextWebSocketFrame(msg);
//                    ch.writeAndFlush(frame);
//                }
//            }

//            while (true) {
//                String msg = console.readLine();
//                if (msg == null) {
//                    break;
//                } else if ("bye".equals(msg.toLowerCase())) {
//                    ch.writeAndFlush(new CloseWebSocketFrame());
//                    ch.closeFuture().sync();
//                    break;
//                } else if ("ping".equals(msg.toLowerCase())) {
//                    WebSocketFrame frame = new PingWebSocketFrame(Unpooled.wrappedBuffer(new byte[] { 8, 1, 8, 1 }));
//                    ch.writeAndFlush(frame);
//                } else {
//                    WebSocketFrame frame = new TextWebSocketFrame(msg);
//                    ch.writeAndFlush(frame);
//                }
//            }


        } finally {
            group.shutdownGracefully();
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
