package com.sdnelson.msc.research.lcf4j.nodemgmt.multicast;

import com.sdnelson.msc.research.lcf4j.core.NodeData;
import com.sdnelson.msc.research.lcf4j.core.NodeRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.string.StringDecoder;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class MulticastHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    final static Logger logger = Logger.getLogger(MulticastHandler.class);

    DatagramPacket comm;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        logger.info("received.");
        logger.info(msg.sender().getPort());
        logger.info(msg.sender().getHostName() + " -> " +msg.recipient().getHostName());
     //   logger.info(new String(ByteBufUtil.getBytes(msg.content())).trim());
        ByteArrayInputStream baos = new ByteArrayInputStream(ByteBufUtil.getBytes(msg.content()));
        ObjectInputStream oos = new ObjectInputStream(baos);
        NodeData nodeData = (NodeData)oos.readObject();
        logger.info(nodeData);
        if(!NodeRegistry.contains(nodeData.getNodeName())){
            NodeRegistry.addActiveNode(nodeData);
            ctx.channel().writeAndFlush(getDatagramPacket());
        }
//        logger.info("[ Node Count : " + NodeRegistry.getActiveNodeCount() + "] " +
//                "[ Active Node List : " + NodeRegistry.getActiveNodeDataList() + "]");
//        logger.info("[ Node Count : " + NodeRegistry.getActiveNodeCount() + "] [ Active Node List : " + NodeRegistry.getActiveNodeKeyList() + "]");
//        logger.info("[ Node Count : " + NodeRegistry.getFailedNodeCount() + "] [ Global Node List : " + NodeRegistry.getFailedNodeKeyList() + "]");


    }

    @Override public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        logger.info("Reg");
      //  ctx.channel().writeAndFlush(comm);
    }

    @Override public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.info("Active");
        ctx.channel().writeAndFlush(getDatagramPacket());
    }

    private DatagramPacket getDatagramPacket() throws IOException {
        ByteBuf buf = Unpooled.buffer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        final NodeData nodeData = new NodeData("lcf4j03", "");
        oos.writeObject(nodeData);
        oos.flush();
        buf.writeBytes(baos.toByteArray());
        DatagramPacket dgram =
                new DatagramPacket(buf,
                        new InetSocketAddress("239.255.27.1", 11209),
                        new InetSocketAddress("abc123", 11209));
        NodeRegistry.addActiveNode(nodeData);
        return dgram;
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        logger.info("UnReg");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        logger.info("InActive");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        logger.info("RComp");
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
        logger.info("WriteChange");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        logger.info("EventTrigger");
    }
}
