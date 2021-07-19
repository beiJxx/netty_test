package com.nic.nio;

import java.nio.ByteBuffer;

/**
 * Description:
 * 转只读Buffer
 *
 * @author james
 * @date 2021/7/19 14:45
 */
public class NIOByteBufferReadonly
{
    public static void main(String[] args) {

        ByteBuffer byteBuffer = ByteBuffer.allocate(64);
        for (int i = 0; i < byteBuffer.capacity(); i++) {
            byteBuffer.put((byte) i);
        }

        byteBuffer.flip();

        ByteBuffer readOnlyBuffer = byteBuffer.asReadOnlyBuffer();
        System.out.println(readOnlyBuffer.getClass());

        while (readOnlyBuffer.hasRemaining()) {
            System.out.println(readOnlyBuffer.get());
        }
        System.out.println("get end...");

        readOnlyBuffer.put((byte) 1);//抛异常 ReadOnlyBufferException

    }
}
