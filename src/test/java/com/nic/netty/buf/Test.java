package com.nic.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Description:
 *
 * @author james
 * @date 2021/7/30 13:50
 */
public class Test
{
    @org.junit.Test
    public void testFramesDecoded2() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        ByteBuf input = buf.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(
                new FixedLengthFrameDecoder(3));
        System.out.println(channel.writeInbound(input.readBytes(2)));
        System.out.println(channel.writeInbound(input.readBytes(7)));
        assertTrue(channel.finish());

        ByteBuf read = (ByteBuf) channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        read = (ByteBuf) channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        read = (ByteBuf) channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        assertNull(channel.readInbound());
        buf.release();
    }

    @org.junit.Test
    public void Test5_12() {
        ByteBuf byteBuf = Unpooled.copiedBuffer("Netty in Action rocks!", StandardCharsets.UTF_8);
        System.out.println((char) byteBuf.getByte(0));

        int readerIndex = byteBuf.readerIndex();
        int writerIndex = byteBuf.writerIndex();

        byteBuf.setByte(0, (byte) 'B');
        System.out.println((char) byteBuf.getByte(0));

        //从readerIndex开始读取24位的整形值，（该类型并非java基本类型，通常不用）
        //        System.out.println(byteBuf.readMedium());
        //从readerIndex开始读取24位的无符号整形值，（该类型并非java基本类型，通常不用）
        //        System.out.println(byteBuf.readUnsignedMedium());

        assert readerIndex == byteBuf.readerIndex();
        assert writerIndex == byteBuf.writerIndex();
    }

}
