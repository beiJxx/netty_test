package com.nic.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * Description:
 *
 * @author james
 * @date 2021/7/21 16:11
 */
public class HttpServer
{
    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup wrokerGroup = new NioEventLoopGroup(4);

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, wrokerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>()
                    {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //加入一个netty 提供的httpServerCodec codec =>[coder - decoder]
                            //HttpServerCodec 说明
                            //1. HttpServerCodec 是netty 提供的处理http的 编-解码器
                            pipeline.addLast("MyHttpServerCodec", new HttpServerCodec());
                            pipeline.addLast("MyHttpServerHandler", new HttpServerHandler());
                            System.out.println("服务器 is ready...");
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(7101).sync();
            channelFuture.channel().closeFuture().sync();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            bossGroup.shutdownGracefully();
            wrokerGroup.shutdownGracefully();
        }
    }

}
