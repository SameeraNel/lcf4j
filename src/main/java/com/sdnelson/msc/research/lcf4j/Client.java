package com.sdnelson.msc.research.lcf4j;

/**
 * Created by sn40157 on 11/1/17.
 */
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

/**
 * Connects to a server periodically to measure and print the uptime of the
 * server.  This example demonstrates how to implement reliable reconnection
 * mechanism in Netty.
 */
public final class Client {

    final static Logger logger = Logger.getLogger(Client.class);
    // Sleep 5 seconds before a reconnection attempt.
    static final int RECONNECT_DELAY = Integer.parseInt(System.getProperty("reconnectDelay", "1"));
    // Reconnect when the server sends nothing for 10 seconds.
    private final int READ_TIMEOUT = Integer.parseInt(System.getProperty("readTimeout", "1"));
    private ClientHandler handler;
    private Bootstrap bootstrap;
    private static EventLoopGroup group = new NioEventLoopGroup(1);

    public Client(String host, int port) {
        handler = new ClientHandler(this);
        bootstrap = new Bootstrap();
        handler = new ClientHandler(this);
        bootstrap.group(group)
                 .remoteAddress(host, port)
                 .channel(NioSocketChannel.class)
                 .handler(new ChannelInitializer<SocketChannel>() {
              @Override
              protected void initChannel(SocketChannel ch) throws Exception {
                  ch.pipeline().addLast(new IdleStateHandler(READ_TIMEOUT, 0, 0), handler);
              }
          });
        bootstrap.connect();
        logger.info("Client started @ " + host + ":" + port);
    }

    void connect() {
        bootstrap.connect().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.cause() != null) {
                    handler.startTime = -1;
                    handler.println("Failed to connect: " + future.cause());
                }
            }
        });
    }
}