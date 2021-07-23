package com.nic.netty.rpc.provider;

import com.nic.netty.rpc.service.IHelloService;
import org.apache.commons.lang3.StringUtils;

/**
 * Description:
 *
 * @author james
 * @date 2021/7/23 14:34
 */
public class HelloServiceImpl implements IHelloService
{

    private static int count = 0;

    @Override
    public String hello(String msg) {
        System.out.println("收到客户的消息：" + msg);
        String respMsg = "你好客户端，我已经收到你的消息";
        if (StringUtils.isNotBlank(msg)) {
            return String.format("%s [%s] 第%s次", respMsg, msg, (++count));
        }
        else {
            return respMsg;
        }
    }
}
