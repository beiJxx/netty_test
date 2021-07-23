package com.nic.netty.rpc.handler;

import com.nic.netty.rpc.consumer.ClientBootstrap;
import com.nic.netty.rpc.provider.HelloServiceImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Description:
 *
 * @author james
 * @date 2021/7/23 13:54
 */
public class RpcNettyServerHandler extends ChannelInboundHandlerAdapter
{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("msg = " + msg);
        //客户端调用服务器api时，需要定义一个协议
        //比如我们要求每次发消息都必须以某个字符串为开头"HelloRPC#hello#"
        if (msg.toString().startsWith(ClientBootstrap.PROVICER_NAME)) {
            String result = new HelloServiceImpl()
                    .hello(msg.toString().substring(msg.toString().lastIndexOf("#") + 1));
            ctx.writeAndFlush(result);
        }
    }
}
