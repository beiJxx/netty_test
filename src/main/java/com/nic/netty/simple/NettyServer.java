package com.nic.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Description:
 * netty服务端
 *
 * @author james
 * @date 2021/7/21 14:34
 */
public class NettyServer
{
    public static void main(String[] args) {

        //创建Bose Group和Worker Group
        // 1. 创建两个线程组bossGroup和workerGroup
        // 2. bossGroup处理连接，workerGroup处理业务
        // 3. 两个group无限循环
        // 4. bossGroup 1个线程，workerGroup 4个线程
        // 不填线程数，默认NettyRuntime.availableProcessors() * 2，即本机cpu核数*2
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(4);

        try {
            //创建服务端启动对象，配置参数
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)//配置两个线程组
                    .channel(NioServerSocketChannel.class)//使用NioServerSocketChannel作为服务器通道实现
                    .option(ChannelOption.SO_BACKLOG, 128)//设置线程队列等待的连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true)//设置保持活动链接状态
                    .childHandler(new ChannelInitializer<SocketChannel>()//创建一个通道初始化对象
                    {
                        //给pipeline设置处理器
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            System.out.println("客户socketChannel hashcode = " + socketChannel.hashCode());
                            //可以使用一个集合管理 SocketChannel，再推送消息时，可以将业务加入到各个channel对应的NIOEventLoop的taskQueue或者scheduleTaskQueue
                            //                            socketChannel.pipeline().addLast(new NettyServerHandler());
                            socketChannel.pipeline().addLast(new NettyServerTaskHandler());
                        }
                    });
            System.out.println("服务器 is ready...");

            //绑定端口并同步，生成channelFuture对象
            ChannelFuture channelFuture = serverBootstrap.bind(7100).sync();

            channelFuture.addListener(new ChannelFutureListener()
            {
                @Override
                public void operationComplete(ChannelFuture cf) throws Exception {
                    if (cf.isSuccess()) {
                        System.out.println("监听端口 7100 成功");
                    }
                    else {
                        System.out.println("监听端口 7100 失败");
                    }
                }
            });
            //对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

}
