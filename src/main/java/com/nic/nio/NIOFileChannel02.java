package com.nic.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Description:
 * 使用 FileChannel（通道）和方法 read、write，拷贝文件file01.txt
 *
 * @author james
 * @date 2021/7/19 14:17
 */
public class NIOFileChannel02
{
    public static void main(String[] args) {
        FileInputStream fileInputStream = null;
        FileChannel inputStreamChannel = null;
        FileOutputStream fileOutputStream = null;
        FileChannel outputStreamChannel = null;
        try {
            fileInputStream = new FileInputStream("d:\\file01.txt");
            inputStreamChannel = fileInputStream.getChannel();

            fileOutputStream = new FileOutputStream("d:\\file02.txt");
            outputStreamChannel = fileOutputStream.getChannel();

            //创建缓冲区，并设置容量
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            //循环读取
            while (true) {
                //每次读取buffer中的内容之后都需要清空buffer
                byteBuffer.clear();
                int read = inputStreamChannel.read(byteBuffer);
                System.out.println("read = " + read);

                //读完了就退出
                if (read == -1) {
                    break;
                }

                // 切换，将buffer的数据写入file02.txt
                byteBuffer.flip();
                outputStreamChannel.write(byteBuffer);
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (null != inputStreamChannel) {
                    inputStreamChannel.close();
                }
                if (null != outputStreamChannel) {
                    outputStreamChannel.close();
                }
                if (null != fileInputStream) {
                    fileInputStream.close();
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
