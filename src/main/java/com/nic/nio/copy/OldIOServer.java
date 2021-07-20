package com.nic.nio.copy;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Description:
 * 传统IO文件传输服务端
 *
 * @author james
 * @date 2021/7/20 14:10
 */
public class OldIOServer
{
    public static final int PORT = 7002;

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        InputStream inputStream = null;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("OldIOServer 已启动。。。");
            //连接
            Socket socket = serverSocket.accept();
            //获取文件流
            inputStream = new BufferedInputStream(socket.getInputStream());
            byte[] flush = new byte[1024];
            long len = 0L;
            long sum = len;
            while ((len = inputStream.read(flush)) != -1) {
                sum += len;
            }
            System.out.println("sum=" + sum);

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (null != inputStream) {
                    inputStream.close();
                }
                if (null != serverSocket) {
                    serverSocket.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
