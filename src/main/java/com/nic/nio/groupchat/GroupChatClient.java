package com.nic.nio.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Description:
 *
 * @author james
 * @date 2021/7/20 8:28
 */
public class GroupChatClient
{
    public static final String HOST = "127.0.0.1";
    public static final int PORT = 7000;
    private Selector selector;
    private SocketChannel socketChannel;
    private String username;

    public GroupChatClient() throws IOException {
        selector = Selector.open();
        socketChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
        socketChannel.configureBlocking(false);
        //注册到selector，并且设置为读操作
        socketChannel.register(selector, SelectionKey.OP_READ);
        username = socketChannel.getLocalAddress().toString().substring(1);

        System.out.println(username + " is ok!");
    }

    public static void main(String[] args) throws IOException {
        GroupChatClient groupChatClient = new GroupChatClient();
        //该线程目的是读取其他客户端的消息，循环读取通道中的消息
        new Thread(() -> {
            while (true) {
                groupChatClient.readInfo();
                try {
                    Thread.sleep(3000L);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            groupChatClient.sendInfo(scanner.nextLine());
        }
    }

    public void sendInfo(String msg) {
        msg = username + " 说：" + msg;
        try {
            socketChannel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读信息逻辑与服务端类似
     * 获取选择器key的个数
     * 如果个数大于0，则挨个处理，把key对应的通道取出来，将通道中的信息读到buffer中，然后输出
     * 如果个数不大于0，则等待
     */
    public void readInfo() {

        try {
            int select = selector.select();
            if (select > 0) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        int read = channel.read(byteBuffer);
                        String msg = new String(byteBuffer.array(), 0, read);
                        System.out.println(msg);
                    }
                    iterator.remove();
                }
            }
            else {
                System.out.println("无可用通道。。。");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
