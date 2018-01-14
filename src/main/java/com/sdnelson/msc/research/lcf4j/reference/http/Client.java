package com.sdnelson.msc.research.lcf4j.reference.http;

/**
 * Created by sn40157 on 11/1/17.
 */
import com.sdnelson.msc.research.lcf4j.core.NodeRegistry;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
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
    static final int RECONNECT_DELAY = Integer.parseInt(System.getProperty("reconnectDelay", "0"));
    // Reconnect when the server sends nothing for 10 seconds.
    private final int READ_TIMEOUT = Integer.parseInt(System.getProperty("readTimeout", "1"));
    private ClientHandler handler;
    private Bootstrap bootstrap;
    private static EventLoopGroup group = new NioEventLoopGroup(1);
    String  clientHost;
    int clientPort;

    public Client(String host, int port) throws InterruptedException {
        handler = new ClientHandler(this);
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                 .remoteAddress(host, port)
                 .channel(NioSocketChannel.class)
                 .handler(new ChannelInitializer<SocketChannel>() {
              @Override
              protected void initChannel(SocketChannel ch) throws Exception {
                  ch.pipeline().addLast(new IdleStateHandler(READ_TIMEOUT, 0, 0), handler);
              }
          });
        ChannelFuture channelFuture = bootstrap.connect();

        clientHost = host;
        clientPort = port;
        logger.info("Client started @ " + host + ":" + port);
        while(true){
//            Collection<NodeData> activeNodeDataList = NodeRegistry.getActiveNodeDataList();
              String hashtext = "";
//            for (NodeData dataList : activeNodeDataList) {
//                MessageDigest md = null;
//                try {
//                    md = MessageDigest.getInstance("MD5");
//                } catch (NoSuchAlgorithmException e) {
//                    e.printStackTrace();
//                }
//                byte[] messageDigest = md.digest(dataList.toString().getBytes());
//                BigInteger number = new BigInteger(1, messageDigest);
//                hashtext += number.toString(16);
//            }
            channelFuture.channel().writeAndFlush(new TextWebSocketFrame("NodeData Hash : " + hashtext + ":" +" Node Count : " + NodeRegistry.getNodeCount()));
            Thread.sleep(1000);
        }
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