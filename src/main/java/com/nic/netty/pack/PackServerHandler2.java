package com.nic.netty.pack;

import cn.hutool.core.util.RandomUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.nio.charset.StandardCharsets;

/**
 * Description:
 * 拆包粘包演示服务端自定义handler,自定义消息协议
 *
 * @author james
 * @date 2021/7/23 10:02
 */
public class PackServerHandler2 extends SimpleChannelInboundHandler<MessageProtocol>
{
    private int count;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {
        int len = msg.getLen();
        byte[] content = msg.getContent();

        String message = new String(content, CharsetUtil.UTF_8);
        System.out.println("服务端收到消息长度：" + len + ",消息内容：" + message + ",消息数量：" + (++this.count));

        //服务端收到消息之后，回写客户端
        //会写客户端需要转成MessageProtocol
        String randomString = RandomUtil.randomString(10);
        byte[] respContent = randomString.getBytes(StandardCharsets.UTF_8);
        MessageProtocol messageProtocol = new MessageProtocol(respContent.length, respContent);
        ctx.writeAndFlush(messageProtocol);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
