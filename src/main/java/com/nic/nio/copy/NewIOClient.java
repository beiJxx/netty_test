package com.nic.nio.copy;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * Description:
 * nio传输文件客户端
 *
 * @author james
 * @date 2021/7/20 13:54
 */
public class NewIOClient
{
    public static final String FILE = "D:\\download\\Programs\\ideaIU-2021.1.3.exe";

    public static final String HOST = "127.0.0.1";
    public static final int PORT = 7001;

    public static void main(String[] args) {
        send();
    }

    public static void send() {
        SocketChannel socketChannel = null;
        FileInputStream fileInputStream = null;
        FileChannel inputChannel = null;
        long count = 0L;
        long sum = count;
        long start = System.currentTimeMillis();
        try {
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress(HOST, PORT));
            fileInputStream = new FileInputStream(FILE);
            inputChannel = fileInputStream.getChannel();
            long size = inputChannel.size();
            long split = 1024 * 1024 * 8;
            long c = size / split;
            //window下最大传输8M，将文件分割8M一份
            for (int i = 0; i <= c; i++) {
                count = inputChannel.transferTo(split * i, split, socketChannel);
                sum += count;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (null != inputChannel) {
                    inputChannel.close();
                }
                if (null != fileInputStream) {
                    fileInputStream.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("NewIO 发送字节：" + sum + "， 耗时：" + (end - start) + " ms");
        }
    }

}
