package com.nic.netty.pack;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Description:
 * 拆包粘包演示客户端
 *
 * @author james
 * @date 2021/7/23 9:58
 */
public class PackClient
{
    private String host;
    private int port;

    public PackClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) {
        new PackClient("127.0.0.1", 7200).run();
    }

    public void run() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>()
                    {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline
                                    //                                    .addLast(new PackClientHandler());
                                    .addLast(new MessageEncoder())
                                    .addLast(new MessageDecoder())
                                    .addLast(new PackClientHandler2());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            channelFuture.channel().closeFuture().sync();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            group.shutdownGracefully();
        }
    }
}
