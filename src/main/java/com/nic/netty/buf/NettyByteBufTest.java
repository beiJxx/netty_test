package com.nic.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

/**
 * Description:
 * ByteBuf举例说明测试
 *
 * @author james
 * @date 2021/7/22 10:59
 */
public class NettyByteBufTest
{
    public static void main(String[] args) {
        //创建一个ByteBuf
        //说明
        //1. 创建 对象，该对象包含一个数组arr , 是一个byte[10]
        //2. 在netty 的buffer中，不需要使用flip 进行反转，底层维护了 readerindex 和 writerIndex
        //3. 通过 readerindex 和  writerIndex 和  capacity， 将buffer分成三个区域
        // 0---readerindex              已经读取的区域
        // readerindex---writerIndex ， 可读的区域
        // writerIndex -- capacity,     可写的区域

        ByteBuf buffer = Unpooled.buffer(10);

        for (int i = 0; i < 10; i++) {
            buffer.writeByte(i);
        }

        System.out.println("capacity = " + buffer.capacity());

        for (int i = 0; i < buffer.capacity(); i++) {
            //两者输出相同
            System.out.println(buffer.getByte(i));// 0 1 2 3 4 5 6 7 8 9
            System.out.println(buffer.readByte());// 0 1 2 3 4 5 6 7 8 9
            System.out.println("-----------------");
        }

        System.out.println("=========================================");
        ByteBuf byteBuf = Unpooled.copiedBuffer("hello,world!", CharsetUtil.UTF_8);
        if (byteBuf.hasArray()) {
            byte[] array = byteBuf.array();

            System.out.println(new String(array, CharsetUtil.UTF_8));

            System.out.println("byteBuf = " + byteBuf);

            //偏移量
            System.out.println(byteBuf.arrayOffset());
            //当前读到的位置
            System.out.println(byteBuf.readerIndex());
            //当前可写的位置
            System.out.println(byteBuf.writerIndex());
            //buf的容量
            System.out.println(byteBuf.capacity());

            //执行readByte之后，readerIndex会+1
            //            System.out.println(byteBuf.readByte());
            //            System.out.println(byteBuf.readerIndex());
            System.out.println(byteBuf.getByte(0));

            //可读字节数
            //以上调用readByte()之后，可读字节数会-1
            System.out.println("len = " + byteBuf.readableBytes());

            for (int i = 0; i < byteBuf.readableBytes(); i++) {
                System.out.println((char) byteBuf.getByte(i));
            }

            //自定义开头和长度读取buf中的内容
            System.out.println(byteBuf.getCharSequence(0, 4, CharsetUtil.UTF_8));
            System.out.println(byteBuf.getCharSequence(4, 8, CharsetUtil.UTF_8));

        }
    }

}
