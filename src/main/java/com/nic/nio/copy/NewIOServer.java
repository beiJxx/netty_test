package com.nic.nio.copy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Description:
 * nio传输文件服务端
 *
 * @author james
 * @date 2021/7/20 13:50
 */
public class NewIOServer
{
    public static final int PORT = 7001;

    public static void main(String[] args) {
        listen();
    }

    public static void listen() {
        try {
            //获得一个ServerSocketChannel通道
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            //绑定端口
            serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
            System.out.println("NewIOServer 已启动。。。");
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            while (true) {
                SocketChannel socketChannel = serverSocketChannel.accept();
                int readcount = 0;
                long sum = readcount;
                while (-1 != readcount) {
                    try {
                        readcount = socketChannel.read(buffer);
                        sum += readcount;
                    }
                    catch (IOException e) {
                        break;
                    }
                    buffer.rewind();
                }
                System.out.println("sum=" + sum);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
