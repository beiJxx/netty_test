package com.nic.netty.rpc.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * Description:
 *
 * @author james
 * @date 2021/7/28 13:52
 */
public class OutHandler1 extends ChannelOutboundHandlerAdapter
{
    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        System.out.println("OutHandler1 read");
        super.read(ctx);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("OutHandler1 write");
        msg += "111111111111111111111111";
        super.write(ctx, msg, promise);
    }
}
