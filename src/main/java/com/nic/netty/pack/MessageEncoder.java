package com.nic.netty.pack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Description:
 *
 * @author james
 * @date 2021/7/23 10:29
 */
public class MessageEncoder extends MessageToByteEncoder<MessageProtocol>
{
    @Override
    protected void encode(ChannelHandlerContext ctx, MessageProtocol msg, ByteBuf out) throws Exception {
        System.out.println("MessageEncoder...");
        out.writeInt(msg.getLen());
        out.writeBytes(msg.getContent());
    }
}
