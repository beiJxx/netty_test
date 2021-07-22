package com.nic.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;

/**
 * Description:
 * 重写ChannelInboundHandlerAdapter中的读数据、数据读取完成、异常处理方法。
 *
 * @author james
 * @date 2021/7/21 15:41
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter
{

    /**
     * @param ctx 上下文对象，含有管道pipeline、通道channel、地址
     * @param msg 客户端发送的数据
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("服务器读取线程 " + Thread.currentThread().getName());
        Channel channel = ctx.channel();
        System.out.println("server ctx = " + ctx + ", channel = " + channel);
        //                                    System.out.println("看看channel 和 pipeline的关系");
        //                                    Channel channel = ctx.channel();
        //                                    ChannelPipeline pipeline = ctx.pipeline(); //本质是一个双向链接, 出站入站

        //将 msg 转成一个 ByteBuf
        //ByteBuf 是 Netty 提供的，不是 NIO 的 ByteBuffer.
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("客户端发送消息是:" + buf.toString(CharsetUtil.UTF_8));
        System.out.println("客户端地址:" + channel.remoteAddress());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws InterruptedException {
        //将数据写入缓存并刷新
        System.out.println("channelReadComplete");
        //此时这边有一个耗时任务，如果直接处理，那服务器这边就会阻塞，应使用任务的方式，不阻塞服务器，详见com.nic.netty.simple.NettyServerTaskHandler

        //        Thread.sleep(20 * 1000);
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello,netty客户端", Charset.defaultCharset()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        //抛异常之后需要关闭
        ctx.close();
    }
}
