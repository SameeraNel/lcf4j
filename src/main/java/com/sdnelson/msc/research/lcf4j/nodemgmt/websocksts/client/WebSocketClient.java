package com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.client;

import com.sdnelson.msc.research.lcf4j.core.NodeData;
import com.sdnelson.msc.research.lcf4j.core.NodeRegistry;
import com.sdnelson.msc.research.lcf4j.util.ClusterConfig;
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
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URI;
import java.security.MessageDigest;
import java.util.Collection;

public final class WebSocketClient {

    final static org.apache.log4j.Logger logger = Logger.getLogger(WebSocketClient.class);
    static final String URL = System.getProperty("url", "ws://127.0.0.1:8010/websocket");
    public static final String WEB_SOCKETS_SCHEME = "ws";
    public static final String LOCALHOST_ADDRESS = "127.0.0.1";
    public static final String WSSWEB_SOCKETS_SECURE_SCHEME = "wss";
    public static final String ONLY_WS_S_IS_SUPPORTED = "Only WS(S) is supported.";

    public static void main(String[] args) throws Exception {
        new WebSocketClient().startClient("localhost", 8080);
    }

    public void startClient(final String host, final int port) throws Exception {
        logger.info("Node Client is starting ...");
        URI uri = new URI(URL);
        String scheme = uri.getScheme() == null? WEB_SOCKETS_SCHEME : uri.getScheme();
//        final String host = uri.getHost() == null? LOCALHOST_ADDRESS : uri.getHost();
//        final int port;
//        if (uri.getPort() == -1) {
//            if (WEB_SOCKETS_SCHEME.equalsIgnoreCase(scheme)) {
//                port = 80;
//            } else if (WSSWEB_SOCKETS_SECURE_SCHEME.equalsIgnoreCase(scheme)) {
//                port = 443;
//            } else {
//                port = -1;
//            }
//        } else {
//            port = uri.getPort();
//        }

        if (!WEB_SOCKETS_SCHEME.equalsIgnoreCase(scheme) &&
                !WSSWEB_SOCKETS_SECURE_SCHEME.equalsIgnoreCase(scheme)) {
            System.err.println(ONLY_WS_S_IS_SUPPORTED);
            return;
        }

        final boolean ssl = WSSWEB_SOCKETS_SECURE_SCHEME.equalsIgnoreCase(scheme);
        final SslContext sslCtx;
        if (ssl) {
            sslCtx = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        EventLoopGroup group = new NioEventLoopGroup(ClusterConfig.getClientSlaveThreadpoolSize());
        ChannelFuture sync;
        try {
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
            logger.info("Client boot sequence initiated...");
            Channel ch = b.connect(uri.getHost(), port).sync().channel();
            logger.info("Client boot sequence completed...");

            handler.handshakeFuture().sync();
            sync = handler.handshakeFuture().sync();

            while (true) {
                WebSocketFrame frame = new TextWebSocketFrame(ch.localAddress().toString());
                ch.writeAndFlush(frame);
                Thread.sleep(1000);
                BufferedReader console = new BufferedReader(new InputStreamReader(System.in));


                while (true) {
                    String hashtext = "";
                    Collection<NodeData> activeNodeDataList = NodeRegistry.getActiveNodeDataList();
                    for (NodeData dataList : activeNodeDataList) {
                        MessageDigest md = MessageDigest.getInstance("MD5");
                        byte[] messageDigest = md.digest(dataList.toString().getBytes());
                        BigInteger number = new BigInteger(1, messageDigest);
                        hashtext += number.toString(16);
                    }
                    ch.writeAndFlush(new TextWebSocketFrame("NodeData Hash : " + hashtext + ":" + " Node Count : " + NodeRegistry.getActiveNodeCount()));
                    Thread.sleep(1000);
                }
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


            } finally{
                group.shutdownGracefully();
            }
        }
}
