package com.nic.netty.rpc.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;

/**
 * Description:
 *
 * @author james
 * @date 2021/7/23 13:59
 */
public class RpcNettyClientHandler extends ChannelInboundHandlerAdapter implements Callable
{
    //上下文
    private ChannelHandlerContext context;
    //返回结果
    private String result;
    //客户端调用方法时传入的参数
    private String param;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive...");
        context = ctx;
    }

    //synchronized
    //同步处理，读完数据之后notify
    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("3.channelRead...");
        result = msg.toString();
        notify();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    @Override
    public synchronized Object call() throws Exception {
        System.out.println("2.call 1 ...");
        context.writeAndFlush(param);
        wait();
        System.out.println("4.call 2 ...");
        return result;
    }

    void setParam(String param) {
        System.out.println("1.setParam...");
        this.param = param;
    }
}
