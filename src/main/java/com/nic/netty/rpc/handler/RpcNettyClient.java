package com.nic.netty.rpc.handler;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author james
 * @date 2021/7/23 14:04
 */
public class RpcNettyClient
{
    private static final ExecutorService POOL = new ThreadPoolExecutor(1, 200,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024),
            new ThreadFactoryBuilder().setNamePrefix("nettyClient-pool-%d").build(),
            new ThreadPoolExecutor.AbortPolicy());
    private static RpcNettyClientHandler clientHandler;
    private int count;

    public Object getBean(final Class<?> serviceClass, final String providerName) {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[] {serviceClass}, new InvocationHandler()
                {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("(proxy,method,args) 进入..." + (++count) + "次");
                        if (null == clientHandler) {
                            initClient();
                        }

                        clientHandler.setParam(providerName + args[0]);

                        return POOL.submit(clientHandler).get();
                    }
                });
    }

    public static void initClient() {
        clientHandler = new RpcNettyClientHandler();
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>()
                {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new StringDecoder())
                                .addLast(new StringEncoder())
                                .addLast(clientHandler);
                    }
                });
        try {
            bootstrap.connect("127.0.0.1", 7300).sync();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
