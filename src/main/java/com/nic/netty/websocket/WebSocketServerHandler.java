package com.nic.netty.websocket;

import cn.hutool.core.date.DateUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * Description:
 *
 * @author james
 * @date 2021/7/22 14:58
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame>
{
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        System.out.println("channelRead0 = " + msg.text());

        ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器时间：" + DateUtil.now() + " " + msg.text()));
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //id表示唯一的值， longText唯一，shortText不唯一
        System.out.println("handlerAdded  long id = " + ctx.channel().id().asLongText());
        System.out.println("handlerAdded short id = " + ctx.channel().id().asShortText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerRemoved long id = " + ctx.channel().id().asLongText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exceptionCaught = " + cause.getMessage());
        ctx.close();
    }
}
