package com.nic.netty.pack;

import cn.hutool.core.util.RandomUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * Description:
 * 拆包粘包演示服务端自定义handler
 *
 * @author james
 * @date 2021/7/23 10:02
 */
public class PackServerHandler extends SimpleChannelInboundHandler<ByteBuf>
{
    private int count;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        byte[] bytes = new byte[msg.readableBytes()];
        msg.readBytes(bytes);

        String message = new String(bytes, CharsetUtil.UTF_8);
        System.out.println("服务端收到消息：" + message);
        System.out.println("服务端收到消息数量：" + (++this.count));

        //服务端收到消息之后，回写客户端
        ByteBuf respBuf = Unpooled.copiedBuffer(RandomUtil.randomString(10) + " ", CharsetUtil.UTF_8);
        ctx.writeAndFlush(respBuf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
