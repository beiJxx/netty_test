package com.nic.netty.pack;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.nio.charset.StandardCharsets;

/**
 * Description:
 * 拆包粘包演示客户端自定义handler，自定义消息协议
 *
 * @author james
 * @date 2021/7/23 10:37
 */
public class PackClientHandler2 extends SimpleChannelInboundHandler<MessageProtocol>
{
    private int count;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive");
        for (int i = 0; i < 10; i++) {
            //这边写数据需要转成MessageProtocol
            String msg = "hello,server " + i;
            byte[] content = msg.getBytes(StandardCharsets.UTF_8);
            MessageProtocol messageProtocol = new MessageProtocol(content.length, content);
            ctx.writeAndFlush(messageProtocol);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {
        int len = msg.getLen();
        byte[] content = msg.getContent();

        String message = new String(content, CharsetUtil.UTF_8);
        System.out.println("客户端收到消息长度：" + len + ",消息内容：" + message + ",消息数量：" + (++this.count));

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause.getMessage());
        ctx.close();
    }
}
