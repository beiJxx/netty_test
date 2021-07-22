package com.nic.netty.groupchat;

import cn.hutool.core.date.DateUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Description:
 * SimpleChannelInboundHandler和ChannelInboundHandlerAdapter区别：
 * SimpleChannelInboundHandler会负责释放指向保存该消息的ByteBuf的内存引用。
 * 而ChannelInboundHandlerAdapter在其时间节点上不会释放消息，而是将消息传递给下一个ChannelHandler处理。
 *
 * @author james
 * @date 2021/7/22 11:26
 */
public class GroupChatServerHandler extends SimpleChannelInboundHandler<String>
{

    //channel组，管理所有channel
    //GlobalEventExecutor.INSTANCE 全局时间执行器，单例
    private static final ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    //以下重写方法执行顺序
    // handlerAdd -> channelActive -> channelInactive -> handlerRemoved
    // 客户端 writeAndFlush之后，触发channelRead0

    /**
     * 连接建立就执行
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println("handlerAdd " + channel.remoteAddress());
        //将上线的客户推送给其他客户端
        //此方法会将group中所有的channel都写一遍
        CHANNEL_GROUP.writeAndFlush("[客户端] " + channel.remoteAddress() + " 加入聊天 " + DateUtil.now() + " \n");
        CHANNEL_GROUP.add(channel);
    }

    /**
     * 断开连接就执行
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println("handlerRemoved " + channel.remoteAddress());
        CHANNEL_GROUP.writeAndFlush("[客户端] " + channel.remoteAddress() + " 退出聊天 " + DateUtil.now() + " \n");
        System.out.println("channelGroup size = " + CHANNEL_GROUP.size());
    }

    /**
     * 表示channel处于活动状态，与handlerAdded区分开
     * 先channelActive，然后handlerAdd
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive " + ctx.channel().remoteAddress());
        System.out.println(ctx.channel().remoteAddress() + " 上线了 " + DateUtil.now());
    }

    /**
     * channel处于非活动状态，与handlerRemoved区分开
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive " + ctx.channel().remoteAddress());
        System.out.println(ctx.channel().remoteAddress() + " 离线了 " + DateUtil.now());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel channel = ctx.channel();
        System.out.println("channelRead0 " + channel.remoteAddress());
        CHANNEL_GROUP.forEach(cg -> {
            //判断是否是当前channel，不是就转发消息
            if (cg != channel) {
                cg.writeAndFlush("[客户端] " + channel.remoteAddress() + " 发送了消息 " + msg + " " + DateUtil.now() + "\n");
            }
            else {
                cg.writeAndFlush("[自己] 发送了消息" + msg + " " + DateUtil.now());
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
