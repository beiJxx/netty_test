package com.nic.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Description:
 * WebSocket 实现长连接服务端
 *
 * @author james
 * @date 2021/7/22 14:54
 */
public class WebSocketServer
{
    private int port;

    public WebSocketServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        new WebSocketServer(7103).listen();
    }

    public void listen() {

        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>()
                    {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline
                                    //基于http协议
                                    .addLast(new HttpServerCodec())
                                    //以块方式写，添加ChunkedWriteHandler处理器
                                    .addLast(new ChunkedWriteHandler())
                                    //http传输过程是分段的，HttpObjectAggregator可以将多个段聚合
                                    .addLast(new HttpObjectAggregator(8 * 1024))
                                    .addLast(new WebSocketServerProtocolHandler("/hello"))
                                    .addLast(new WebSocketServerHandler());
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

}
