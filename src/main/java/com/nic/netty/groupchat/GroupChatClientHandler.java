package com.nic.netty.groupchat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Description:
 *
 * @author james
 * @date 2021/7/22 13:49
 */
public class GroupChatClientHandler extends SimpleChannelInboundHandler<String>
{
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("client channelRead0 = " + msg.trim());
    }
}
