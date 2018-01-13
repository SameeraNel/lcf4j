package com.sdnelson.msc.research.lcf4j.nodemgmt.multicast;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;

public class MCast {

    private static final String LOCAL_ADDR = "localhost";
    private static final String MCAST_GROUP = "239.254.42.96";
    private static final int MCAST_PORT = 9796;

    public static void main(String[] args) throws Exception {
        Thread sender = new Thread(new Sender());
        Thread receiver = new Thread(new Receiver());

        receiver.start();
        sender.start();

        sender.join();
        receiver.join();
    }

    private static class MCastSupport {
        protected InetAddress localAddr;
        protected InetAddress remoteAddr;
        protected InetSocketAddress localSocketAddr;
        protected InetSocketAddress remoteSocketAddr;
        protected DatagramChannel chan;
        protected Bootstrap bootstrap;

        public MCastSupport() {
            try {
                localAddr = InetAddress.getByName(LOCAL_ADDR);
                remoteAddr = InetAddress.getByName(MCAST_GROUP);

                localSocketAddr = new InetSocketAddress(localAddr, MCAST_PORT);
                remoteSocketAddr = new InetSocketAddress(remoteAddr, MCAST_PORT);

                NetworkInterface nif = NetworkInterface.getByInetAddress(localAddr);

                bootstrap = new Bootstrap()
                        .group(new NioEventLoopGroup())
                        .handler(new SimpleChannelInboundHandler<DatagramPacket>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
                                System.out.println("Received: " + msg.content().getInt(0));
                            }
                        })
                        .channelFactory(new ChannelFactory<NioDatagramChannel>() {
                            @Override
                            public NioDatagramChannel newChannel() {
                                return new NioDatagramChannel(InternetProtocolFamily.IPv4);
                            }
                        })
                        .handler(new SimpleChannelInboundHandler<DatagramPacket>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
                                System.out.println("Received: " + msg.content().getInt(0));
                            }
                        })
                        .option(ChannelOption.SO_BROADCAST, true)
                        .option(ChannelOption.SO_REUSEADDR, true)
                        .option(ChannelOption.IP_MULTICAST_LOOP_DISABLED, true)
                        .option(ChannelOption.SO_RCVBUF, 2048)
                        .option(ChannelOption.IP_MULTICAST_TTL, 255)
                        .option(ChannelOption.IP_MULTICAST_IF, nif);

                chan = (DatagramChannel) bootstrap.bind(localSocketAddr).sync().channel();

                chan.joinGroup(remoteSocketAddr, nif).sync();

            } catch (Throwable t) {
                System.err.println(t);
                t.printStackTrace(System.err);
            }
        }
    }

    private static class Sender extends MCastSupport implements Runnable {
        @Override
        public void run() {
            try {
                for (int seq = 1; seq <= 5; ++ seq) {
                    ByteBuf buf = Unpooled.copyInt(seq);
                    DatagramPacket dgram = new DatagramPacket(buf, remoteSocketAddr, localSocketAddr);
                    chan.writeAndFlush(dgram);
                    System.out.println("Send: " + seq);
                    Thread.sleep(5000);
                }

            } catch (Throwable t) {
                System.err.println(t);
                t.printStackTrace(System.err);
            }
        }
    }

    private static class Receiver extends MCastSupport implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(5 * 5000);
            } catch (Throwable t) {
                System.err.println(t);
                t.printStackTrace(System.err);
            }
        }
    }
}