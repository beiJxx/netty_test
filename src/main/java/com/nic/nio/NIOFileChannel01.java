package com.nic.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * Description:
 * 通过ByteBuffer和FileChannel将数据写入本地文件
 * 通过ByteBuffer和FileChannel读取本地文件中的内容
 *
 * @author james
 * @date 2021/7/19 13:57
 */
public class NIOFileChannel01
{
    public static final String FILE_NAME = "d:\\file01.txt";

    public static void main(String[] args) {

        writeFile();

        readFile();
    }

    public static void readFile() {
        FileInputStream fileInputStream = null;
        FileChannel channel = null;

        try {
            File file = new File(FILE_NAME);
            //创建文件输入流
            fileInputStream = new FileInputStream(file);

            //通过 fileInputStream 获取对应的 FileChannel -> 实际类型 FileChannelImpl
            channel = fileInputStream.getChannel();

            //创建缓冲区，容量为文件的大小
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());

            //将通道的数据读到buffer
            channel.read(byteBuffer);

            //将byteBuffer的字节数据转成string
            System.out.println(new String(byteBuffer.array()));

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (null != channel) {
                    channel.close();
                }
                if (null != fileInputStream) {
                    fileInputStream.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeFile() {
        String str = "hello，netty学习";
        FileOutputStream fileOutputStream = null;
        FileChannel channel = null;
        try {
            //创建输出流 fileOutputStream
            fileOutputStream = new FileOutputStream(FILE_NAME);

            //通过fileOutputStream获取对应的FileChannel
            //这个FileChannel真实类型是FileChannelImpl
            channel = fileOutputStream.getChannel();

            //创建缓冲区，并设置容量
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            //将字符串放入缓冲区
            byteBuffer.put(str.getBytes(StandardCharsets.UTF_8));

            //通过flip切换
            byteBuffer.flip();

            //将缓冲区数据写入fileChannel
            channel.write(byteBuffer);

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (null != channel) {
                    channel.close();
                }
                if (null != fileOutputStream) {
                    fileOutputStream.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
