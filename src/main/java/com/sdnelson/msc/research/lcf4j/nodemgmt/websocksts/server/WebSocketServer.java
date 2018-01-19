package com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.server;

import com.sdnelson.msc.research.lcf4j.cache.CacheRegistry;
import com.sdnelson.msc.research.lcf4j.core.NodeData;
import com.sdnelson.msc.research.lcf4j.nodemgmt.NodeRegistry;
import com.sdnelson.msc.research.lcf4j.core.NodeStatus;
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class WebSocketServer {

    final static org.apache.log4j.Logger logger = Logger.getLogger(WebSocketServer.class);
    public final String SSL_SCHEME = "ssl";
//    final boolean SSL = System.getProperty(SSL_SCHEME) != null;
    final boolean SSL = true;
    final ExecutorService serverListener = Executors.newFixedThreadPool(1);



    public static void main(String[] args) throws Exception {
        ClusterConfig.initClusterConfig();
        new WebSocketServer().startServer("localhost", 8444);
    }

    public void startServer(final String serverName, final int port) throws Exception {
        logger.info("Node Server is Starting @ " + serverName + ":"+ port + " ...");
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            logger.info("WSS Scheme initiated.");
        } else {
            sslCtx = null;
        }
        EventLoopGroup bossGroup = new NioEventLoopGroup(ClusterConfig.getServerMasterThreadpoolSize());
        EventLoopGroup workerGroup = new NioEventLoopGroup(ClusterConfig.getServerSlaveThreadpoolSize());
        Runnable listeningTask = () -> {
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .handler(new LoggingHandler(LogLevel.DEBUG))
             .childHandler(new WebSocketServerInitializer(sslCtx));

            logger.info("Server Boot Sequence Initiated @ " + serverName + ":"+ port + " ...");
            Channel ch = b.bind(port).sync().channel();
            final NodeData nodeData = new NodeData(serverName, ch.localAddress().toString());
            nodeData.setStatus(NodeStatus.PASSIVE);
            logger.info("Server Boot Sequence Completed @ " + serverName + ":"+ port + " ...");
            logger.info("Server Node Data Record Created : " + nodeData.toString());
            Runnable listeningTask2 = () -> {
                try {
                    ch.closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
            nodeData.setStatus(NodeStatus.ACTIVE);
            NodeRegistry.addActiveNode(nodeData);
            serverListener.execute(listeningTask2);
            while (true) {
                NodeRegistry.refreshLastUpdated(serverName);
                CacheRegistry.getCacheMap();
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
        };
        serverListener.execute(listeningTask);

    }
}
