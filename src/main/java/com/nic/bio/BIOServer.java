package com.nic.bio;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Description:
 *
 * @author james
 * @date 2021/7/16 16:14
 */
public class BIOServer
{
    public static void main(String[] args) throws IOException {

        //1.创建一个线程池
        //org.apache.commons.lang3.concurrent.BasicThreadFactory
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("bioserver-schedule-pool-%d").daemon(true).build());

        //2.如果有客户端连接，就创建一个线程与之通信（单独写个方法）
        ServerSocket serverSocket = new ServerSocket(6666);
        System.out.println("服务器启动了...");
        while (true) {
            System.out.println("线程信息id = " + Thread.currentThread().getId() + " 名字 = " + Thread.currentThread().getName());
            //监听,等待客户端连接
            System.out.println("等待连接");

            Socket socket = serverSocket.accept();
            System.out.println("连接到一个客户端");
            //创建一个线程与之通信
            executorService.execute(() -> {
                handler(socket);
            });

        }
    }

    //与客户端通信的方法
    public static void handler(Socket socket) {
        try {
            System.out.println("线程信息id = " + Thread.currentThread().getId() + " 名字 = " + Thread.currentThread().getName());
            byte[] bytes = new byte[1024];
            InputStream inputStream = socket.getInputStream();
            while (true) {
                System.out.println("线程信息id = " + Thread.currentThread().getId() + " 名字 = " + Thread.currentThread().getName());
                System.out.println("read...");
                int read = inputStream.read(bytes);
                if (read != -1) {
                    System.out.println(new String(bytes, 0, read));
                }
                else {
                    break;
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            System.out.println("close client的连接");
            try {
                socket.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
