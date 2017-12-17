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
package com.sdnelson.msc.research.lcf4j.http;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Keep reconnecting to the server while printing out the current uptime and
 * connection attempt getStatus.
 */
@Sharable
public class UptimeClientHandler extends SimpleChannelInboundHandler<Object> {

    final static Logger logger = Logger.getLogger(UptimeClientHandler.class);
    long startTime = -1;
    Long errorTime = -1L;
    private String host;
    private int port;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (startTime < 0) {
            startTime = System.currentTimeMillis();
        }
//        println("Heartbeat received.");
        println("");
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
            println("Heart beat completed.");
            ctx.close();
        }
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
        println("Heartbeat completed.");
    }

    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx) throws Exception {
        println("Sleeping for: " + UptimeClient.RECONNECT_DELAY + 's');

        ctx.channel().eventLoop().schedule(new Runnable() {
            public void run() {
               println("Heartbeat starting...");
                UptimeClient.connect();
            }
        }, UptimeClient.RECONNECT_DELAY, TimeUnit.SECONDS);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        try {
            channelUnregistered(ctx);
        } catch (Exception e) {
            ctx.close();
        }
    }

    void println(String msg) {
        if (startTime < 0) {
            long errorMillis = System.currentTimeMillis() - errorTime;
            final String formattedErrorString =
                    String.format("[ "+ host + ":" + port +" ] [DOWNTIME] [%d Days %d Hours %d Minutes %d Seconds] %s",
                                  MILLISECONDS.toDays(errorMillis),
                                  MILLISECONDS.toHours(errorMillis) - TimeUnit.DAYS.toHours(MILLISECONDS.toDays(errorMillis)),
                                  MILLISECONDS.toMinutes(errorMillis) - TimeUnit.HOURS.toMinutes(MILLISECONDS.toHours(errorMillis)),
                                  MILLISECONDS.toSeconds(errorMillis) - TimeUnit.MINUTES.toSeconds(MILLISECONDS.toMinutes(errorMillis)),msg);
//            logger.info(formattedErrorString);
            logger.info("[SERVER IS DOWN] %s%n " + msg);
        } else {
            synchronized (errorTime) {
                errorTime = -1L;
            }
            long millis = System.currentTimeMillis() - startTime;
            final String formattedString = String.format(
                    "[ "+ host + ":" + port +" ] [ UPTIME ] [%d Days %d Hours %d Minutes %d Seconds] %s",
                    MILLISECONDS.toDays(millis),
                    MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(MILLISECONDS.toDays(millis)),
                    MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(MILLISECONDS.toHours(millis)),
                    MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(MILLISECONDS.toMinutes(millis)), msg);

//            logger.info(formattedString);
            logger.info("[UPTIME: %5ds] %s%n " + msg);
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
