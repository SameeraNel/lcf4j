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

    final static Logger logger = Logger.getLogger(App.class);

    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8010"));
    // Sleep 5 seconds before a reconnection attempt.
    static final int RECONNECT_DELAY = Integer.parseInt(System.getProperty("reconnectDelay", "1"));
    // Reconnect when the server sends nothing for 10 seconds.
    private static final int READ_TIMEOUT = Integer.parseInt(System.getProperty("readTimeout", "1"));

    private static final ClientHandler handler = new ClientHandler();
    private static final Bootstrap bs = new Bootstrap();


    public void connectClient(){
        EventLoopGroup group = new NioEventLoopGroup();
        bs.group(group)
          .channel(NioSocketChannel.class)
          .remoteAddress(HOST, PORT)
          .handler(new ChannelInitializer<SocketChannel>() {
              @Override
              protected void initChannel(SocketChannel ch) throws Exception {
                  ch.pipeline().addLast(new IdleStateHandler(READ_TIMEOUT, 0, 0), handler);
              }
          });
        bs.connect();
        logger.info("Client started @ " + HOST + ":" + PORT);
    }

    static void connect() {
        bs.connect().addListener(new ChannelFutureListener() {
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