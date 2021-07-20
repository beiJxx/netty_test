package com.nic.nio.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * Description:
 * 群聊系统服务端，用于接收客户端消息，并实现转发（处理上线、离线）
 *
 * @author james
 * @date 2021/7/19 16:53
 */
public class GroupChatServer
{
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

    public static final int PORT = 7000;

    public GroupChatServer() {
        try {
            //获得选择器
            selector = Selector.open();
            //获得一个ServerSocketChannel通道
            serverSocketChannel = ServerSocketChannel.open();
            //绑定端口
            serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
            //设置非阻塞模式
            serverSocketChannel.configureBlocking(false);
            //注册到selector，并且设置为连接已建立
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        GroupChatServer groupChatServer = new GroupChatServer();
        groupChatServer.listen();
    }

    public void listen() {
        System.out.println("listen...");
        try {
            while (true) {
                //得到选择器的数量
                int select = selector.select();
                if (select > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        //获取selectionKey
                        SelectionKey key = iterator.next();
                        //判断是否监听到了accept
                        if (key.isAcceptable()) {
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            socketChannel.configureBlocking(false);
                            //注册到selector，并且设置为读操作
                            socketChannel.register(selector, SelectionKey.OP_READ);
                            System.out.println(socketChannel.getRemoteAddress() + "上线了。。。");
                        }
                        //判断通道是否可读，如果可读就调用读数据方法
                        if (key.isReadable()) {
                            readData(key);
                        }
                        //处理完就删掉，防止重复处理
                        iterator.remove();
                    }
                }
                else {
                    //没有选择器的情况下就等待客户端
                    System.out.println("等客户端上线。。。");
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 从客户端读取消息
     *
     * @param key
     */
    public void readData(SelectionKey key) {

        SocketChannel socketChannel = null;
        try {
            //得到与之关联的通道
            socketChannel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            //将通道中的数据读入buffer
            int read = socketChannel.read(buffer);
            if (read > 0) {
                String msg = new String(buffer.array(), 0, read);
                System.out.println("from 客户端： " + msg);

                //向其他客户端转发消息
                sendInfo2OtherClients(msg, socketChannel);
            }

        }
        catch (IOException e) {
            //            e.printStackTrace();
            try {
                System.out.println(socketChannel.getRemoteAddress() + "离线了。。。");
                //取消注册
                key.cancel();
                //关闭通道
                socketChannel.close();
            }
            catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }

    /**
     * 转发消息给其他客户（通道）
     *
     * @param msg
     * @param socketChannel
     */
    public void sendInfo2OtherClients(String msg, SocketChannel socketChannel) throws IOException {
        System.out.println("sendInfo2OtherClients 服务器转发消息中。。。");

        for (SelectionKey k : selector.keys()) {//通过keys取出对应的SocketChannel
            SelectableChannel targetChannel = k.channel();
            //排除自己
            if (targetChannel instanceof SocketChannel && targetChannel != socketChannel) {
                SocketChannel destChannel = (SocketChannel) targetChannel;
                //将msg存到buffer
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8));
                //将msg写到目标用户
                destChannel.write(buffer);
            }
        }
    }

}
