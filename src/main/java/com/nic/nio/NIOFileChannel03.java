package com.nic.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Description:
 * 使用 FileChannel（通道）和方法 transferFrom，完成文件的拷贝
 *
 * @author james
 * @date 2021/7/19 14:25
 */
public class NIOFileChannel03
{
    public static void main(String[] args) {
        {
            FileInputStream fileInputStream = null;
            FileOutputStream fileOutputStream = null;
            FileChannel inputStreamChannel = null;
            FileChannel outputStreamChannel = null;
            try {
                fileInputStream = new FileInputStream("d:\\png01.png");
                inputStreamChannel = fileInputStream.getChannel();

                fileOutputStream = new FileOutputStream("d:\\png02.png");
                outputStreamChannel = fileOutputStream.getChannel();

                outputStreamChannel.transferFrom(inputStreamChannel, 0, inputStreamChannel.size());

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

}
