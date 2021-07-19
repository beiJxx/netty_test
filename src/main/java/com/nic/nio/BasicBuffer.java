package com.nic.nio;

import java.nio.IntBuffer;

/**
 * Description:
 *
 * @author james
 * @date 2021/7/16 17:10
 */
public class BasicBuffer
{
    public static void main(String[] args) {

        IntBuffer intBuffer = IntBuffer.allocate(5);
        for (int i = 0; i < intBuffer.capacity(); i++) {
            intBuffer.put(i * 2);
        }

        intBuffer.flip();
        while (intBuffer.hasRemaining()) {
            System.out.println(intBuffer.get());
        }

    }
}
