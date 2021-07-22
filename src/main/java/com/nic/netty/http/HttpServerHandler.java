package com.nic.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

import java.net.URI;

/**
 * Description:
 * SimpleChannelInboundHandler<I> extends ChannelInboundHandlerAdapter
 * HttpObject 客户端和服务器端相互通讯的数据被封装成 HttpObject
 *
 * @author james
 * @date 2021/7/21 16:14
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject>
{

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject httpObject) throws Exception {
        System.out.println("对应的channel=" + ctx.channel() + " pipeline=" + ctx.pipeline() + " 通过pipeline获取channel" + ctx.pipeline().channel());

        System.out.println("当前ctx的handler=" + ctx.handler());

        if (httpObject instanceof HttpRequest) {
            System.out.println("ctx类型：" + ctx.getClass());

            System.out.println("pipeline hashcode = " + ctx.pipeline().hashCode() + " HttpServerHandler hash = " + this.hashCode());
            System.out.println("httpObjec 类型：" + httpObject.getClass());
            System.out.println("客户端地址：" + ctx.channel().remoteAddress());

            HttpRequest httpRequest = (HttpRequest) httpObject;

            URI uri = new URI(httpRequest.uri());
            if ("/favicon.ico".equals(uri.getPath())) {
                System.out.println("请求了favicon.ico，不做相应");
                return;
            }

            //回复信息给浏览器
            ByteBuf buf = Unpooled.copiedBuffer("hellolllllllll", CharsetUtil.UTF_8);
            //构建http相应
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());

            ctx.writeAndFlush(response);

        }

    }
}
