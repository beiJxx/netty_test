package com.nic.nio;

import java.nio.ByteBuffer;

/**
 * Description:
 * put和get类型问题
 *
 * @author james
 * @date 2021/7/19 14:36
 */
public class NIOByteBufferPutGet
{
    public static void main(String[] args) {

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        byteBuffer.putInt(100);
        byteBuffer.putLong(10);
        byteBuffer.putChar('新');
        byteBuffer.putShort((short) 1);

        byteBuffer.flip();

        //按照put的顺序get
        System.out.println(byteBuffer.getInt());
        System.out.println(byteBuffer.getLong());
        System.out.println(byteBuffer.getChar());
        System.out.println(byteBuffer.getShort());
    }

}
