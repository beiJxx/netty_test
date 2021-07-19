package com.nic;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Description:
 *
 * @author james
 * @date 2021/7/16 16:39
 */
public class Client implements Runnable
{

    Socket socket = null;

    public Client(String ip, Integer port) {
        try {
            socket = new Socket(ip, port);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client("127.0.0.1", 7000);
        client.run();
    }

    @Override
    public void run() {
        OutputStream outputStream = null;
        try (Scanner scanner = new Scanner(System.in)) {
            outputStream = socket.getOutputStream();
            String read = "";
            while (true) {
                read = scanner.next();
                outputStream.write(read.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (null != outputStream) {
                    outputStream.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
