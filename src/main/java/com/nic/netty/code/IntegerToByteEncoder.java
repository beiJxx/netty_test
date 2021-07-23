package com.nic.netty.code;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Description:
 *
 * @author james
 * @date 2021/7/23 9:51
 */
public class IntegerToByteEncoder extends MessageToByteEncoder<Integer>
{
    @Override
    protected void encode(ChannelHandlerContext ctx, Integer msg, ByteBuf out) throws Exception {
        //将Integer转成二进制字节流写入ByteBuf中
        out.writeInt(msg);
    }
}
