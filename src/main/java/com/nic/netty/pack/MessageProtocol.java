package com.nic.netty.pack;

/**
 * Description:
 * 自定义消息协议，解决粘包拆包问题
 *
 * @author james
 * @date 2021/7/23 10:28
 */
public class MessageProtocol
{
    //数据包长度
    private int len;

    private byte[] content;

    public MessageProtocol(int len, byte[] content) {
        this.len = len;
        this.content = content;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
