package com.nic.netty.rpc.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Description:
 *
 * @author james
 * @date 2021/7/28 10:52
 */
public class InHandler1 extends ChannelInboundHandlerAdapter
{

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("InHandler1 channelActive");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("InHandler1 channelRead");
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("InHandler1 channelRegistered...");
    }
}
