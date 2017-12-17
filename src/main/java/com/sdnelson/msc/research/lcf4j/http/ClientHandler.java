package com.sdnelson.msc.research.lcf4j.http;


import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * Keep reconnecting to the server while printing out the current uptime and
 * connection attempt getStatus.
 */
@Sharable
public class ClientHandler extends SimpleChannelInboundHandler<Object> {

    final static Logger logger = Logger.getLogger(ClientHandler.class);
    long startTime = -1;
    private Client clientRef;

    public ClientHandler(Client client) {
        clientRef = client;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (startTime < 0) {
            startTime = System.currentTimeMillis();
        }
        println("[ " +ctx.channel().remoteAddress() + " ]");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        // Discard received data
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (!(evt instanceof IdleStateEvent)) {
            return;
        }

        IdleStateEvent e = (IdleStateEvent) evt;
        if (e.state() == IdleState.READER_IDLE) {
            // The connection was OK but there was no traffic for last period.
          //  println("Disconnecting due to no inbound traffic");
            ctx.close();
        }
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
      //  println("Disconnected from: " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx) throws Exception {
      //  println("Sleeping for: " + Client.RECONNECT_DELAY + 's');

        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
              //  println("Reconnecting to: " + Client.HOST + ':' + Client.PORT);
                clientRef.connect();
            }
        }, Client.RECONNECT_DELAY, TimeUnit.SECONDS);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    void println(String msg) {
        if (startTime < 0) {
            logger.error("[" + clientRef.clientHost + ":" + clientRef.clientPort  + "] "+ " [ DOWNTIME ] ");
        } else {
            logger.error("[" + clientRef.clientHost + ":" + clientRef.clientPort  + "] "+ " [ UPTIME ] " );
        }
    }
}
