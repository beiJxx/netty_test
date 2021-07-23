package com.nic.netty.pack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * Description:
 * ReplayingDecoder 扩展了 ByteToMessageDecoder 类，
 * 使用这个类，我们不必调用 readableBytes() 方法。参数 S 指定了用户状态管理的类型，其中 Void 代表不需要状态管理
 *
 * @author james
 * @date 2021/7/23 10:32
 */
public class MessageDecoder extends ReplayingDecoder<Void>
{
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("MessageDecoder...");

        //获取MessageProtocol数据包的长度
        int length = in.readInt();

        byte[] bytes = new byte[length];
        in.readBytes(bytes);

        //封装MessageProtocol对象，放入out，传递给下一个handler
        MessageProtocol messageProtocol = new MessageProtocol(length, bytes);
        out.add(messageProtocol);
    }
}
