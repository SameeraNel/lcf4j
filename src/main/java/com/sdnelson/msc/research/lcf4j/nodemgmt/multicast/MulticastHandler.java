package com.sdnelson.msc.research.lcf4j.nodemgmt.multicast;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.apache.log4j.Logger;

import java.util.Date;

public class MulticastHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    final static Logger logger = Logger.getLogger(MulticastHandler.class);

    DatagramPacket comm;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        logger.info("receive");
        logger.info(msg);
        comm = msg.duplicate();
        Thread.sleep(3000);
        ctx.channel().writeAndFlush("Time : " + new Date());
        ctx.channel().writeAndFlush(msg);
    }

    @Override public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        logger.info("Reg");
        ctx.channel().writeAndFlush("Time : " + new Date());
        ctx.channel().writeAndFlush(comm);
    }

    @Override public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.info("Active");
        ctx.channel().writeAndFlush("Time : " + new Date());
        ctx.channel().writeAndFlush(comm);
    }
}
