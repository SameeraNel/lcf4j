/*
 * Copyright 2017 The Netty Project
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
package com.sdnelson.msc.research.lcf4j;

import com.diogonunes.jcdp.color.ColoredPrinter;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Sharable
public class UptimeServerHandler extends SimpleChannelInboundHandler<Object> {

    ColoredPrinter printer = new ColoredPrinter.Builder(1, false)
            //setting format
            .build();

//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        printer.print(ctx.channel().remoteAddress() + " " + ctx.channel().isActive() + ctx.channel().isActive() + ctx.channel().isActive());
//
//    }
//
//    @Override
//    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
//       // printer.print("channelUnregistered " + ctx.channel().remoteAddress());
//    }
//
//
//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        printer.print("channelInactive " +ctx.channel().remoteAddress());
//    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
//        cause.printStackTrace();
        ctx.close();
    }
}
