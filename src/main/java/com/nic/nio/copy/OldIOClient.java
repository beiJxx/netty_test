package com.nic.nio.copy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Description:
 * 传统IO文件传输客户端
 *
 * @author james
 * @date 2021/7/20 14:04
 */
public class OldIOClient
{
    public static final String FILE = "D:\\download\\Programs\\ideaIU-2021.1.3.exe";
    public static final String HOST = "127.0.0.1";
    public static final int PORT = 7002;

    public static void main(String[] args) {
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        long start = 0L;
        int len = -1;
        long sum = len;
        try {
            socket = new Socket(HOST, PORT);
            //从socket中获取输入流
            inputStream = new BufferedInputStream(new FileInputStream(FILE));
            //创建输出流
            outputStream = new BufferedOutputStream(socket.getOutputStream());

            byte[] bytes = new byte[1024 * 1024 * 8];
            start = System.currentTimeMillis();
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                sum += len;
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (null != outputStream) {
                    outputStream.flush();
                    outputStream.close();
                }
                if (null != inputStream) {
                    inputStream.close();
                }
                if (null != socket) {
                    socket.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("OldIO 发送字节：" + sum + "，耗时：" + (end - start) + " ms");
        }

    }
}
