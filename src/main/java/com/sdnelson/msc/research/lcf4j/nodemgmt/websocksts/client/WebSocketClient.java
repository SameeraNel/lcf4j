package com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.client;

import com.sdnelson.msc.research.lcf4j.nodemgmt.NodeRegistry;
import com.sdnelson.msc.research.lcf4j.core.NodeStatus;
import com.sdnelson.msc.research.lcf4j.util.ClusterConfig;
import com.sdnelson.msc.research.lcf4j.util.WebSocketFrameUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class WebSocketClient {

    final static org.apache.log4j.Logger logger = Logger.getLogger(WebSocketClient.class);
    final String URL = System.getProperty("url", "wss://127.0.0.1:8010/websocket");
    public final String WEB_SOCKETS_SCHEME = "ws";
    public final String LOCALHOST_ADDRESS = "127.0.0.1";
    public final String WSSWEB_SOCKETS_SECURE_SCHEME = "wss";
    public final String ONLY_WS_S_IS_SUPPORTED = "Only WS(S) is supported.";
    ExecutorService serverListener;

    public WebSocketClient(final int threadCount) {
        serverListener = Executors.newFixedThreadPool(threadCount);
    }

    public static void main(String[] args) throws Exception {
        ClusterConfig.initClusterConfig();
        new WebSocketClient(1).startClient("localhost", 8444);
    }

    public void startClient(final String host, final int port) throws Exception {
        final String nodeServerName = ClusterConfig.getNodeServerName();
        logger.info("Node Client is Starting @ " + nodeServerName + " for " + host + ":" + port );
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
            logger.info("WSS Scheme initiated.");
        } else {
            sslCtx = null;
        }

        EventLoopGroup group = new NioEventLoopGroup(ClusterConfig.getClientSlaveThreadpoolSize());
        Runnable runnableTask = () -> {
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
                logger.info("Client Boot Sequence Initiated @ " + nodeServerName + " for " + host + ":" + port );
                Channel ch = b.connect(uri.getHost(), port).sync().channel();
                logger.info("Client Boot Sequence Completed @ " + nodeServerName + " for " + host + ":" + port );
                handler.handshakeFuture().sync();
                ch.writeAndFlush(WebSocketFrameUtil.getRequestClusterWebSocketFrame());
                Thread.sleep(5000);

                while (true) {
                    if(NodeRegistry.getNodeData(nodeServerName) != null &&
                            (NodeStatus.PASSIVE.equals(NodeRegistry.getNodeData(nodeServerName).getStatus()) ||
                            NodeStatus.OFFLINE.equals(NodeRegistry.getNodeData(nodeServerName).getStatus()))){
                        break;
                    }
                    //Request for node sync/ cache and config
                    ch.writeAndFlush(WebSocketFrameUtil.getNodeDataWebSocketFrame());
                    Thread.sleep(5000);
            }

//                while (true) {
//                    WebSocketFrame frame = new TextWebSocketFrame(ch.localAddress().toString());
//                    ch.writeAndFlush(frame);
//                    Thread.sleep(1000);
//                    BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
//
//
//                    while (true) {
//                        String hashtext = "";
//                        Collection<NodeData> activeNodeDataList = NodeRegistry.getNodeData();
//                        for (NodeData dataList : activeNodeDataList) {
//                            MessageDigest md = MessageDigest.getInstance("MD5");
//                            byte[] messageDigest = md.digest(dataList.toString().getBytes());
//                            BigInteger number = new BigInteger(1, messageDigest);
//                            hashtext += number.toString(16);
//                        }
//                        ch.writeAndFlush(new TextWebSocketFrame("NodeData Hash : " + hashtext + ":" + " Node Count : " + NodeRegistry.getNodeCount()));
//                        Thread.sleep(1000);
//                    }
//                }


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

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                group.shutdownGracefully();
            }
        };
        serverListener.execute(runnableTask);
    }
}
