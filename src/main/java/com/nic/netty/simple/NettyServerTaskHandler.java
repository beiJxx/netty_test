package com.nic.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 * 重写ChannelInboundHandlerAdapter中的读数据、数据读取完成、异常处理方法。
 *
 * @author james
 * @date 2021/7/21 15:41
 */
public class NettyServerTaskHandler extends ChannelInboundHandlerAdapter
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

        //---------------------------------------------------------
        // 解决方案1 用户程序自定义的普通任务
        System.out.println("execute 1");
        ctx.channel().eventLoop().execute(new Runnable()
        {
            @Override
            public void run() {

                try {
                    Thread.sleep(5 * 1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端2 " + new Date(), CharsetUtil.UTF_8));
                    System.out.println("channel code=" + ctx.channel().hashCode());
                }
                catch (Exception ex) {
                    System.out.println("发生异常" + ex.getMessage());
                }
            }
        });
        System.out.println("execute 2");
        ctx.channel().eventLoop().execute(new Runnable()
        {
            @Override
            public void run() {

                try {
                    Thread.sleep(5 * 1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端3 " + new Date(), CharsetUtil.UTF_8));
                    System.out.println("channel code=" + ctx.channel().hashCode());
                }
                catch (Exception ex) {
                    System.out.println("发生异常" + ex.getMessage());
                }
            }
        });
        //---------------------------------------------------------

        //---------------------------------------------------------
        //解决方案2 : 用户自定义定时任务 -》 该任务是提交到 scheduleTaskQueue中
        System.out.println("schedule 1");
        ctx.channel().eventLoop().schedule(new Runnable()
        {
            @Override
            public void run() {

                try {
                    Thread.sleep(5 * 1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端4 " + new Date(), CharsetUtil.UTF_8));
                    System.out.println("channel code=" + ctx.channel().hashCode());
                }
                catch (Exception ex) {
                    System.out.println("发生异常" + ex.getMessage());
                }
            }
        }, 5, TimeUnit.SECONDS);
        //---------------------------------------------------------

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        //抛异常之后需要关闭
        ctx.close();
    }
}
