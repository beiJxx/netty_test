package com.nic.netty.rpc.consumer;

import cn.hutool.core.date.DateUtil;
import com.nic.netty.rpc.handler.RpcNettyClient;
import com.nic.netty.rpc.service.IHelloService;

/**
 * Description:
 *
 * @author james
 * @date 2021/7/23 14:17
 */
public class ClientBootstrap
{

    public static final String PROVICER_NAME = "helloRPC#hello#";

    public static void main(String[] args) throws InterruptedException {
        RpcNettyClient consumer = new RpcNettyClient();
        IHelloService service = (IHelloService) consumer.getBean(IHelloService.class, PROVICER_NAME);
        while (true) {
            Thread.sleep(2 * 1000);
            String result = service.hello(DateUtil.now() + " 你好 dubbo！");
            System.out.println("调用结果：" + result);
        }
    }

}
