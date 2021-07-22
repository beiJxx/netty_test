package com.nic.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;

/**
 * Description:
 *
 * @author james
 * @date 2021/7/21 15:42
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter
{
    /**
     * 通道就绪就会触发该方法
     *
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("client = " + ctx);
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello,netty服务端", Charset.defaultCharset()));
    }

    /**
     * 通道有读取事件时触发
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("channelRead");
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("服务器回复：" + buf.toString(Charset.defaultCharset()));
        System.out.println("服务器地址：" + ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
