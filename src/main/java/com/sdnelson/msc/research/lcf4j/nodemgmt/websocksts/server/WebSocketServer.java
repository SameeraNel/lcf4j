package com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.server;

import com.sdnelson.msc.research.lcf4j.core.NodeData;
import com.sdnelson.msc.research.lcf4j.core.NodeRegistry;
import com.sdnelson.msc.research.lcf4j.util.ClusterConfig;
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

public final class WebSocketServer {

    final static org.apache.log4j.Logger logger = Logger.getLogger(WebSocketServer.class);
    public static final String SSL_SCHEME = "ssl";
    static final boolean SSL = System.getProperty(SSL_SCHEME) != null;
    static final String host = ClusterConfig.getNodeServerName();
    static final int port = SSL? ClusterConfig.getNodeServerPortSsl() : ClusterConfig.getNodeServerPort();

    public void startServer() throws Exception {
        logger.info("Node Server is Starting @ " + host + ":"+ port + " ...");
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }
        EventLoopGroup bossGroup = new NioEventLoopGroup(ClusterConfig.getServerMasterThreadpoolSize());
        EventLoopGroup workerGroup = new NioEventLoopGroup(ClusterConfig.getServerSlaveThreadpoolSize());
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .handler(new LoggingHandler(LogLevel.DEBUG))
             .childHandler(new WebSocketServerInitializer(sslCtx));

            logger.info("Server boot sequence initiated.");
            Channel ch = b.bind(port).sync().channel();
            logger.info("Server boot sequence Completed.");
            NodeRegistry.addNode(new NodeData(ch.id().toString(), ClusterConfig.getNodeServerName()));
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
}
