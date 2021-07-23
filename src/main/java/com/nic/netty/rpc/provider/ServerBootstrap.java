package com.nic.netty.rpc.provider;

import com.nic.netty.rpc.handler.RpcNettyServer;

/**
 * Description:
 *
 * @author james
 * @date 2021/7/23 13:50
 */
public class ServerBootstrap
{
    public static void main(String[] args) {
        new RpcNettyServer(7300).listen();
    }
}
