# 一、介绍和使用场景

## 1. Netty介绍

1. `Netty` 是由 `JBOSS` 提供的一个 `Java` 开源框架，现为 `Github` 上的独立项目。
2. `Netty` 是一个异步的、基于事件驱动的网络应用框架，用以快速开发高性能、高可靠性的网络 `IO` 程序。
3. `Netty` 主要针对在 `TCP` 协议下，面向 `Client` 端的高并发应用，或者 `Peer-to-Peer` 场景下的大量数据持续传输的应用。
4. `Netty` 本质是一个 `NIO` 框架，适用于服务器通讯相关的多种应用场景。
5. 要透彻理解 `Netty`，需要先学习 `NIO`，这样我们才能阅读 `Netty` 的源码。

## 2. 使用场景

1. 互联网行业

    1. 互联网行业：在分布式系统中，各个节点之间需要远程服务调用，高性能的 `RPC` 框架必不可少，`Netty` 作为异步高性能的通信框架，往往作为基础通信组件被这些 `RPC` 框架使用。

    2. 典型的应用有：阿里分布式服务框架 `Dubbo` 的 `RPC` 框 架使用 `Dubbo` 协议进行节点间通信，`Dubbo` 协议默认使用 `Netty` 作为基础通信组件，用于实现各进程节点之间的内部通信。

   ![image-20210719090520551](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/image-20210719090520551.png)

2. 游戏行业

    1. 无论是手游服务端还是大型的网络游戏，`Java` 语言得到了越来越广泛的应用。
    2. `Netty` 作为高性能的基础通信组件，提供了 `TCP/UDP` 和 `HTTP` 协议栈，方便定制和开发私有协议栈，账号登录服务器。
    3. 地图服务器之间可以方便的通过 `Netty` 进行高性能的通信。

3. 大数据行业

    1. 经典的 `Hadoop` 的高性能通信和序列化组件 `Avro` 的 `RPC` 框架，默认采用 `Netty` 进行跨界点通信。
    2. 它的 `NettyService` 基于 `Netty` 框架二次封装实现。

# 二、I/O模型

## 1. 什么是I/O

I/O（**I**nput/**O**utpu） 即**输入／输出** 。

- **从计算机结构的角度来看**，计算机结构分为5大部分：运算器、控制器、存储器、输入设备、输出设备。

  **I/O描述了计算机系统与外部设备之间的通信的过程**

  ![image-20210719093337439](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/image-20210719093337439.png)

- **从应用程序的角度来看**，为了保证操作系统的稳定性和安全性，一个进程的地址空间划分为 **用户空间（User space）** 和 **内核空间（Kernel space ）** 。

  像我们平常运行的应用程序都是运行在用户空间，只有内核空间才能进行系统态级别的资源有关的操作，比如如文件管理、进程通信、内存管理等等。也就是说，我们想要进行 IO 操作，一定是要依赖内核空间的能力。

  并且，用户空间的程序不能直接访问内核空间。

  当想要执行 IO 操作时，由于没有执行这些操作的权限，只能发起系统调用请求操作系统帮忙完成。

  因此，用户进程想要执行 IO 操作的话，必须通过 **系统调用** 来间接访问内核空间

  我们在平常开发过程中接触最多的就是 **磁盘 IO（读写文件）**和 **网络 IO（网络请求和相应）**。

  **总之，我们的应用程序对操作系统的内核发起 IO 调用（系统调用），操作系统负责的内核执行具体的 IO 操作。也就是说，我们的应用程序实际上只是发起了 IO 操作的调用而已，具体 IO 的执行是由操作系统的内核来完成的。**

  当应用程序发起 I/O 调用后，会经历两个步骤：

    1. 内核等待 I/O 设备准备好数据
    2. 内核将数据从内核空间拷贝到用户空间。

## 2. 常见的I/O模型

UNIX 系统下， IO 模型一共有 5 种：**同步阻塞 I/O**、**同步非阻塞 I/O**、**I/O 多路复用**、**信号驱动 I/O** 和**异步 I/O**。

**注：本文只讨论UNIX系统下的I/O模型，Windows系统下常见的有阻塞模型、选择模型、WSAAsyncSelect模型、WSAEventSelect模型、重叠模型、完成端口模型。**

1. BIO（Blocking I/O）

   BIO属于同步阻塞I/O模型，在客户端连接数不高的情况下，这种模型是没问题的，但当连接数达到百万级别甚至更高的时候，BIO模型就无法承受如此高的压力，所以就需要更搞笑的I/O处理模型来应对更高的并发。

   ![image-20210719094527092](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/image-20210719094527092.png)

2. NIO（Non-blocking/New I/O）

   `java.nio`包自java 1.4中引入，提供了`Channel` , `Selector`，`Buffer` 等抽象。

   其实NIO可看做I/O 多路复用模型，也可看做同步非阻塞I/O模型，具体介绍可往下看。

    - 同步非阻塞I/O模型，如图所示。

      ![image-20210719095028758](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/image-20210719095028758.png)

      虽然相对于同步阻塞模型，同步非阻塞有了很大改进，通过轮询操作，避免了一直阻塞，**但是应用程序不断进行 I/O 系统调用轮询数据是否已经准备好的过程是十分消耗 CPU 资源的，**所以衍生出如下I/O多路复用模型

    - I/O多路复用模型

      ![](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/Snipaste_2021-07-19_10-12-48.png)

      IO 多路复用模型中，线程首先发起 select 调用，询问内核数据是否准备就绪，等内核把数据准备好了，用户线程再发起 read 调用。read 调用的过程（数据从内核空间->用户空间）还是阻塞的。

      > 目前支持 IO 多路复用的系统调用，有 select，epoll 等等。select 系统调用，是目前几乎在所有的操作系统上都有支持

        - **select 调用** ：内核提供的系统调用，它支持一次查询多个系统调用的可用状态。几乎所有的操作系统都支持。
        - **epoll 调用** ：linux 2.6 内核，属于 select 调用的增强版本，优化了 IO 的执行效率。

      **IO 多路复用模型，通过减少无效的系统调用，减少了对 CPU 资源的消耗。**

      Java 中的 NIO ，有一个非常重要的**选择器 ( Selector )** 的概念，也可以被称为**多路复用器**。通过它，只需要一个线程便可以管理多个客户端连接。当客户端数据到了之后，才会为其服务。【详情见第三章Java NIO编程】

3. AIO（Asynchronous I/O）

   AIO 也就是 NIO 2。Java 7 中引入了 NIO 的改进版 NIO 2,它是异步 IO 模型。

   异步 IO 是基于事件和回调机制实现的，也就是应用操作之后会直接返回，不会堵塞在那里，当后台处理完成，操作系统会通知相应的线程进行后续的操作。

   ![image-20210719102737824](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/image-20210719102737824.png)

> 总结：以上简单介绍了常见的I/O模型，简单总结一下如下图

![image-20210719102929824](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/Snipaste_2021-07-19_10-29-23.png)

# 二、Java BIO编程

## 1. 基本介绍

1. `Java BIO` 就是传统的 `Java I/O` 编程，其相关的类和接口在 `java.io`。
2. `BIO(BlockingI/O)`：同步阻塞，服务器实现模式为一个连接一个线程，即客户端有连接请求时服务器端就需要启动一个线程进行处理，如果这个连接不做任何事情会造成不必要的线程开销，可以通过线程池机制改善（实现多个客户连接服务器）。【后有应用实例】
3. `BIO` 方式适用于连接数目比较小且固定的架构，这种方式对服务器资源要求比较高，并发局限于应用中，`JDK1.4` 以前的唯一选择，程序简单易理解。

## 2. 工作机制

![image-20210719103757887](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/image-20210719103757887.png)

对 `BIO` 编程流程的梳理

1. 服务器端启动一个 `ServerSocket`。
2. 客户端启动 `Socket` 对服务器进行通信，默认情况下服务器端需要对每个客户建立一个线程与之通讯。
3. 客户端发出请求后，先咨询服务器是否有线程响应，如果没有则会等待，或者被拒绝。
4. 如果有响应，客户端线程会等待请求结束后，在继续执行。

## 3. 应用实例

实例说明：

1. 使用 `BIO` 模型编写一个服务器端，监听 `6666` 端口，当有客户端连接时，就启动一个线程与之通讯。

2. 要求使用线程池机制改善，可以连接多个客户端。

3. 服务器端可以接收客户端发送的数据（`telnet` 方式即可）。

4. 代码演示：

   ```java
   package com.nic.bio;
   
   import org.apache.commons.lang3.concurrent.BasicThreadFactory;
   
   import java.io.IOException;
   import java.io.InputStream;
   import java.net.ServerSocket;
   import java.net.Socket;
   import java.util.concurrent.ScheduledExecutorService;
   import java.util.concurrent.ScheduledThreadPoolExecutor;
   
   /**
    * Description:
    *
    * @author james
    * @date 2021/7/16 16:14
    */
   public class BIOServer
   {
       public static void main(String[] args) throws IOException {
   
           //1.创建一个线程池
           //org.apache.commons.lang3.concurrent.BasicThreadFactory
           ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1,
                   new BasicThreadFactory.Builder().namingPattern("bioserver-schedule-pool-%d").daemon(true).build());
   
           //2.如果有客户端连接，就创建一个线程与之通信（单独写个方法）
           ServerSocket serverSocket = new ServerSocket(6666);
           System.out.println("服务器启动了...");
           while (true) {
               System.out.println("线程信息id = " + Thread.currentThread().getId() + " 名字 = " + Thread.currentThread().getName());
               //监听,等待客户端连接
               System.out.println("等待连接");
   
               Socket socket = serverSocket.accept();
               System.out.println("连接到一个客户端");
               //创建一个线程与之通信
               executorService.execute(() -> {
                   handler(socket);
               });
   
           }
       }
   
       //与客户端通信的方法
       public static void handler(Socket socket) {
           try {
               System.out.println("线程信息id = " + Thread.currentThread().getId() + " 名字 = " + Thread.currentThread().getName());
               byte[] bytes = new byte[1024];
               InputStream inputStream = socket.getInputStream();
               while (true) {
                   System.out.println("线程信息id = " + Thread.currentThread().getId() + " 名字 = " + Thread.currentThread().getName());
                   System.out.println("read...");
                   int read = inputStream.read(bytes);
                   if (read != -1) {
                       System.out.println(new String(bytes, 0, read));
                   }
                   else {
                       break;
                   }
               }
           }
           catch (IOException e) {
               e.printStackTrace();
           }
           finally {
               System.out.println("close client的连接");
               try {
                   socket.close();
               }
               catch (Exception e) {
                   e.printStackTrace();
               }
           }
       }
   }
   ```

## 4. 问题分析

1. 每个请求都需要创建独立的线程，与对应的客户端进行数据 `Read`，业务处理，数据 `Write`。
2. 当并发数较大时，需要创建大量线程来处理连接，系统资源占用较大。
3. 连接建立后，如果当前线程暂时没有数据可读，则线程就阻塞在 `Read` 操作上，造成线程资源浪费。

# 三、Java NIO编程

## 1. 基本介绍

1. `Java NIO` 全称 **`Java non-blocking IO`** ，是指 `JDK` 提供的新 `API`。从 `JDK1.4` 开始，`Java` 提供了一系列改进的输入/输出的新特性，被统称为 `NIO`（即 `NewIO`），是同步非阻塞的。

2. `NIO` 有三大核心部分: **`Channel`（通道）、`Buffer`（缓冲区）、`Selector`（选择器）** 。

3. `NIO` 是**面向缓冲区，或者面向块编程**的。数据读取到一个它稍后处理的缓冲区，需要时可在缓冲区中前后移动，这就增加了处理过程中的灵活性，使用它可以提供非阻塞式的高伸缩性网络。

4. `Java NIO` 的非阻塞模式，使一个线程从某通道发送请求或者读取数据，但是它仅能得到目前可用的数据，如果目前没有数据可用时，就什么都不会获取，而不是保持线程阻塞，所以直至数据变的可以读取之前，该线程可以继续做其他的事情。非阻塞写也是如此，一个线程请求写入一些数据到某通道，但不需要等待它完全写入，这个线程同时可以去做别的事情。【后面有案例说明】

5. 通俗理解：`NIO` 是可以做到用一个线程来处理多个操作的。假设有 `10000` 个请求过来,根据实际情况，可以分配 `50` 或者 `100` 个线程来处理。不像之前的阻塞 `IO` 那样，非得分配 `10000` 个。

6. `HTTP 2.0` 使用了多路复用的技术，做到同一个连接并发处理多个请求，而且并发请求的数量比 `HTTP 1.1` 大了好几个数量级。

7. 部分代码说明

   ```java
   //举例说明 Buffer 的使用(简单说明)
   //创建一个 Buffer，大小为 5，即可以存放 5 个 int
   IntBuffer intBuffer = IntBuffer.allocate(5);
   
   //向buffer存放数据
   //intBuffer.put(10);
   //intBuffer.put(11);
   //intBuffer.put(12);
   //intBuffer.put(13);
   //intBuffer.put(14);
   for (int i = 0; i < intBuffer.capacity(); i++) {
       intBuffer.put(i * 2);
   }
   //如何从 buffer 读取数据
   //将 buffer 转换，读写切换(!!!)
   intBuffer.flip();
   while (intBuffer.hasRemaining()) {
       System.out.println(intBuffer.get());
   }
   ```

## 2. NIO和BIO比较

1. `BIO` 以流的方式处理数据，而 `NIO` 以块的方式处理数据，块 `I/O` 的效率比流 `I/O` 高很多。
2. `BIO` 是阻塞的，`NIO` 则是非阻塞的。
3. `BIO` 基于字节流和字符流进行操作，而 `NIO` 基于 `Channel`（通道）和 `Buffer`（缓冲区）进行操作，数据总是从通道读取到缓冲区中，或者从缓冲区写入到通道中。`Selector`（选择器）用于监听多个通道的事件（比如：连接请求，数据到达等），因此使用单个线程就可以监听多个客户端通道。

## 3. NIO三大核心

NIO三大核心 `Selector`、`Channel` 和 `Buffer`

**Selector、Channel、Buffer关系图：**

![image-20210719105821558](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/image-20210719105821558.png)

1. 每个 `Channel` 都会对应一个 `Buffer`。
2. `Selector` 对应一个线程，一个线程对应多个 `Channel`（连接）。
3. 该图反应了有三个 `Channel` 注册到该 `Selector` （程序）
4. 程序切换到哪个 `Channel` 是由事件决定的，`Event` 就是一个重要的概念。
5. `Selector` 会根据不同的事件，在各个通道上切换。
6. `Buffer` 就是一个内存块，底层是有一个数组。
7. 数据的读取写入是通过 `Buffer`，这个和 `BIO`不同，**`BIO` 中要么是输入流，或者是输出流，不能双向，但是 `NIO` 的 `Buffer` 是可以读也可以写**，需要 `flip` 方法切换 `Channel` 是双向的，可以返回底层操作系统的情况，比如 `Linux`，底层的操作系统通道就是双向的。

### 3.1 缓冲区（Buffer）

缓冲区（`Buffer`）：缓冲区本质上是一个**可以读写数据的内存块**，可以理解成是一个**容器对象（含数组）**，该对象提供了一组方法，可以更轻松地使用内存块，缓冲区对象内置了一些机制，能够跟踪和记录缓冲区的状态变化情况。`Channel` 提供从文件、网络读取数据的渠道，但是读取或写入的数据都必须经由 `Buffer`，如下图所示

![image-20210719110444240](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/image-20210719110444240.png)

**Buffer源码分析**

1. 在 `NIO` 中，`Buffer` 是一个顶层父类，它是一个抽象类，类的层级关系图：

   ![image-20210719113213697](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/image-20210719113213697.png)

2. `Buffer` 类定义了所有的缓冲区都具有的四个属性来提供关于其所包含的数据元素的信息：

   ![image-20210719134050342](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/image-20210719134050342.png)

3. `Buffer` 类相关方法一览

   ![image-20210719134530888](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/image-20210719134530888.png)

4. `java`中基本类型都有对应的一个`Buffer`类（除了Boolean），最常用的是`ByteBuffer`类（二进制数据），该类对应的主要方法如下

   ![image-20210719134825959](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/image-20210719134825959.png)

### 3.2 通道（Channel）

#### 3.2.1 基本介绍

1. `NIO`的通道类似于流，但有些区别如下：
    - 通道可以**同时进行读写**，而流只能读或者只能写
    - 通道可以实现**异步读写数据**
    - 通道可以从缓冲读数据，也可以写数据到缓冲
2. `BIO` 中的 `Stream` 是单向的，例如 `FileInputStream` 对象只能进行读取数据的操作，而 `NIO` 中的通道（`Channel`）是双向的，可以读操作，也可以写操作。
3. `Channel` 在 `NIO` 中是一个接口 `public interface Channel extends Closeable{}`
4. 常用的 `Channel` 类有：**`FileChannel`、`DatagramChannel`、`ServerSocketChannel` 和 `SocketChannel`**。【`ServerSocketChannel` 类似 `ServerSocket`、`SocketChannel` 类似 `Socket`】
5. `FileChannel` 用于文件的数据读写，`DatagramChannel` 用于 `UDP` 的数据读写，`ServerSocketChannel` 和 `SocketChannel` 用于 `TCP` 的数据读写。

![image-20210719135306087](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/image-20210719135306087.png)

#### 3.2.2 FileChannel类

`FileChannel` 主要用来对本地文件进行 `IO` 操作，常见的方法有

- `public int read(ByteBuffer dst)`，从通道读取数据并放到缓冲区中
- `public int write(ByteBuffer src)`，把缓冲区的数据写到通道中
- `public long transferFrom(ReadableByteChannel src, long position, long count)`，从目标通道中复制数据到当前通道
- `public long transferTo(long position, long count, WritableByteChannel target)`，把数据从当前通道复制给目标通道

#### 3.2.3 应用实例

##### 3.2.3.1 本地文件写数据，然后读出来显示

> 使用`ByteBuffer`和`FileChannel`，将"hello，新点"，写入file01.txt中
>
> 使用`ByteBuffer`和`FileChannel`，读取file01.txt中的内容并输出

代码如下：

```java
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

```

##### 3.2.3.2 使用一个Buffer完成文件读取、写入

> 使用 `FileChannel`（通道）和方法 `read、write`，拷贝文件1.txt

![image-20210719141719231](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/image-20210719141719231.png)

```java
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

```

##### 3.2.3.3 使用transferFrom方法实现拷贝

> 使用 `FileChannel`（通道）和方法 `transferFrom`，完成文件的拷贝（图片）

```java
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
```

### 3.3 选择器（Selector）

1. `Java` 的 `NIO`，用非阻塞的 `IO` 方式。可以用一个线程，处理多个的客户端连接，就会使用到 `Selector`（选择器）。
2. `Selector` 能够检测多个注册的通道上是否有事件发生（注意：多个 `Channel` 以事件的方式可以注册到同一个 `Selector`），如果有事件发生，便获取事件然后针对每个事件进行相应的处理。这样就可以只用一个单线程去管理多个通道，也就是管理多个连接和请求。
3. 只有在连接/通道真正有读写事件发生时，才会进行读写，就大大地减少了系统开销，并且不必为每个连接都创建一个线程，不用去维护多个线程，避免了多线程之间的上下文切换导致的开销

##### 3.3.1 示意图

![image-20210719152918734](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/Snipaste_2021-07-19_15-29-16.png)

1. `Netty` 的 `IO` 线程 `NioEventLoop` 聚合了 `Selector`（选择器，也叫多路复用器），可以同时并发处理成百上千个客户端连接。
2. 当线程从某客户端 `Socket` 通道进行读写数据时，若没有数据可用时，该线程可以进行其他任务。
3. 线程通常将非阻塞 `IO` 的空闲时间用于在其他通道上执行 `IO` 操作，所以单独的线程可以管理多个输入和输出通道。
4. 由于读写操作都是非阻塞的，这就可以充分提升 `IO` 线程的运行效率，避免由于频繁 `I/O` 阻塞导致的线程挂起。
5. 一个 `I/O` 线程可以并发处理 `N` 个客户端连接和读写操作，这从根本上解决了传统同步阻塞 `I/O` 一连接一线程模型，架构的性能、弹性伸缩能力和可靠性都得到了极大的提升。

##### 3.3.2 Selector类相关方法

![image-20210719154527597](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/image-20210719154527597.png)

### 3.4 注意事项和细节

1. `ByteBuffer`支持类型化的`put`和`get`，`put`放入什么类型，`get`就只能按`put`的类型来取

   ```java
   package com.nic.nio;
   
   import java.nio.ByteBuffer;
   
   /**
    * Description:
    * put和get类型问题
    *
    * @author james
    * @date 2021/7/19 14:36
    */
   public class NIOByteBufferPutGet
   {
       public static void main(String[] args) {
   
           ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
   
           byteBuffer.putInt(100);
           byteBuffer.putLong(10);
           byteBuffer.putChar('新');
           byteBuffer.putShort((short) 1);
   
           byteBuffer.flip();
   
           //按照put的顺序get
           System.out.println(byteBuffer.getInt());
           System.out.println(byteBuffer.getLong());
           System.out.println(byteBuffer.getChar());
           System.out.println(byteBuffer.getShort());
       }
   }
   ```

2. 将一个`Buffer`转成`只读Buffer`

   ```java
   package com.nic.nio;
   
   import java.nio.ByteBuffer;
   
   /**
    * Description:
    * 转只读Buffer
    *
    * @author james
    * @date 2021/7/19 14:45
    */
   public class NIOByteBufferReadonly
   {
       public static void main(String[] args) {
   
           ByteBuffer byteBuffer = ByteBuffer.allocate(64);
           for (int i = 0; i < byteBuffer.capacity(); i++) {
               byteBuffer.put((byte) i);
           }
   
           byteBuffer.flip();
   
           ByteBuffer readOnlyBuffer = byteBuffer.asReadOnlyBuffer();
           System.out.println(readOnlyBuffer.getClass());
   
           while (readOnlyBuffer.hasRemaining()) {
               System.out.println(readOnlyBuffer.get());
           }
           System.out.println("get end...");
   
           readOnlyBuffer.put((byte) 1);//抛异常 ReadOnlyBufferException
       }
   }
   
   ```

3. `NIO` 还提供了 `MappedByteBuffer`，可以让文件直接在内存（堆外的内存）中进行修改，而如何同步到文件由 `NIO` 来完成

   ```java
   package com.nic.nio;
   
   import java.io.IOException;
   import java.io.RandomAccessFile;
   import java.nio.MappedByteBuffer;
   import java.nio.channels.FileChannel;
   
   /**
    * Description:
    * NIO 还提供了 MappedByteBuffer，可以让文件直接在内存（堆外的内存）中进行修改，而如何同步到文件由 NIO 来完成。
    * MappedByteBuffer 可让文件直接在内存（堆外内存）修改,操作系统不需要拷贝一次
    *
    * @author james
    * @date 2021/7/19 14:51
    */
   public class MappedByteBufferTest
   {
   
       public static void main(String[] args) {
   
           RandomAccessFile randomAccessFile = null;
           FileChannel channel = null;
           try {
               // "r", "rw", "rws", or "rwd"
               randomAccessFile = new RandomAccessFile("d:\\file01.txt", "rw");
   
               channel = randomAccessFile.getChannel();
   
               /*
                * 参数 1:    FileChannel.MapMode.READ_WRITE 使用的读写模式
                * 参数 2：    0:可以直接修改的起始位置
                * 参数 3:    5:是映射到内存的大小（不是索引位置），即将 1.txt 的多少个字节映射到内存
                * 可以直接修改的范围就是 0-5
                * 实际类型 DirectByteBuffer
                */
               MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 5);
               //hello->Hel9o
               mappedByteBuffer.put(0, (byte) 'H');
               mappedByteBuffer.put(3, (byte) '9');
               mappedByteBuffer.put(5, (byte) 'Y');//超过最大size5，抛异常IndexOutOfBoundsException
           }
           catch (IOException e) {
               e.printStackTrace();
           }
           finally {
               try {
                   if (null != channel) {
                       channel.close();
                   }
                   if (null != randomAccessFile) {
                       randomAccessFile.close();
                   }
               }
               catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }
   }
   ```

4. 前面我们讲的读写操作，都是通过一个 `Buffer` 完成的，`NIO` 还支持通过多个 `Buffer`（即 `Buffer`数组）完成读写操作，即 `Scattering` 和 `Gathering`

   ```java
   package com.nic.nio;
   
   import java.io.IOException;
   import java.net.InetSocketAddress;
   import java.nio.Buffer;
   import java.nio.ByteBuffer;
   import java.nio.channels.ServerSocketChannel;
   import java.nio.channels.SocketChannel;
   import java.util.Arrays;
   
   /**
    * Description:
    * 前面我们讲的读写操作，都是通过一个 Buffer 完成的，NIO 还支持通过多个 Buffer（即 Buffer数组）完成读写操作，即 Scattering 和 Gathering
    * <p>
    * Scattering：将数据写入到 buffer 时，可以采用 buffer 数组，依次写入 [分散]
    * Gathering：从 buffer 读取数据时，可以采用 buffer 数组，依次读
    *
    * @author james
    * @date 2021/7/19 14:59
    */
   public class ScatteringAndGatheringTest
   {
       public static void main(String[] args) throws IOException {
   
           //使用 ServerSocketChannel 和 SocketChannel 网络
           ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
           InetSocketAddress inetSocketAddress = new InetSocketAddress(7000);
   
           //绑定端口到socket
           serverSocketChannel.socket().bind(inetSocketAddress);
   
           ByteBuffer[] byteBuffers = new ByteBuffer[2];
           byteBuffers[0] = ByteBuffer.allocate(5);
           byteBuffers[1] = ByteBuffer.allocate(3);
   
           //等待客户端连接
           SocketChannel socketChannel = serverSocketChannel.accept();
   
           int messageLength = 8;
   
           while (true) {
               int byteRead = 0;
               while (byteRead < messageLength) {
                   long read = socketChannel.read(byteBuffers);
                   byteRead += read;//累计读取的字节数
                   System.out.println("byteRead = " + byteRead);
                   //使用流打印,看看当前的这个 buffer 的 position 和 limit
                   Arrays.stream(byteBuffers).map(buffer -> "position = " + buffer.position() + ", limit = " + buffer.limit()).forEach(System.out::println);
               }
   
               Arrays.stream(byteBuffers).forEach(Buffer::flip);
   
               long byteWrite = 0;
   
               while (byteWrite < messageLength) {
                   long write = socketChannel.write(byteBuffers);
                   byteWrite += write;
               }
   
               Arrays.stream(byteBuffers).forEach(Buffer::clear);
               System.out.println("byteRead = " + byteRead + ", byteWrite = " + byteWrite + ", messagelength = " + messageLength);
           }
   
       }
   }
   ```

   上述代码可通过`Client`来测试

   ```java
   package com.epoint;
   
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
   ```

5. `Selector`相关方法说明

    - `selector.select();` //阻塞
    - `selector.select(1000);` //阻塞 1000 毫秒，在 1000 毫秒后返回
    - `selector.wakeup();` //唤醒 selector
    - `selector.selectNow();` //不阻塞，立马返还

## 4. NIO非阻塞网络编程

### 4.1 关系图梳理

`NIO` 非阻塞网络编程相关的（`Selector`、`SelectionKey`、`ServerScoketChannel` 和 `SocketChannel`）关系梳理图

1. 当客户端连接时，会通过 `ServerSocketChannel` 得到 `SocketChannel`。
2. `Selector` 进行监听 `select` 方法，返回有事件发生的通道的个数。
3. 将 `socketChannel` 注册到 `Selector` 上，`register(Selector sel, int ops)`，一个 `Selector` 上可以注册多个 `SocketChannel`。
4. 注册后返回一个 `SelectionKey`，会和该 `Selector` 关联（集合）。
5. 进一步得到各个 `SelectionKey`（有事件发生）。
6. 在通过 `SelectionKey` 反向获取 `SocketChannel`，方法 `channel()`。
7. 可以通过得到的 `channel`，完成业务处理。

![image-20210719160230375](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/image-20210719160230375.png)

### 4.2 源码解读

#### 4.2.1 `SelectorKey`相关方法与参数

![image-20210719161736956](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/image-20210719161736956.png)

#### 4.2.2 `ServerSocketChannel`相关方法

![image-20210719162357770](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/image-20210719162357770.png)

#### 4.2.3 `SocketChannel`相关方法

![image-20210719163252432](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/image-20210719163252432.png)

### 4.3 编码实战

> `NIO` 群聊系统，实现服务器端和客户端之间的数据简单通讯（非阻塞）,目的为进一步理解`NIO`非阻塞网络编程机制

要求：

1. 多人群聊
2. 服务端：可以检测用户上线、离线，并实现消息转发功能
3. 客户端：通过`Channel`无阻塞发消息给其他所有用户，同时可以接受其他用户发送的消息（由服务器转发得到）

代码如下：

- 服务端代码：

  ```java
  package com.nic.nio.groupchat;
  
  import java.io.IOException;
  import java.net.InetSocketAddress;
  import java.nio.ByteBuffer;
  import java.nio.channels.SelectableChannel;
  import java.nio.channels.SelectionKey;
  import java.nio.channels.Selector;
  import java.nio.channels.ServerSocketChannel;
  import java.nio.channels.SocketChannel;
  import java.nio.charset.StandardCharsets;
  import java.util.Iterator;
  
  /**
   * Description:
   * 群聊系统服务端，用于接收客户端消息，并实现转发（处理上线、离线）
   *
   * @author james
   * @date 2021/7/19 16:53
   */
  public class GroupChatServer
  {
      private Selector selector;
      private ServerSocketChannel serverSocketChannel;
  
      public static final int PORT = 7000;
  
      public GroupChatServer() {
          try {
              //获得选择器
              selector = Selector.open();
              //获得一个ServerSocketChannel通道
              serverSocketChannel = ServerSocketChannel.open();
              //绑定端口
              serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
              //设置非阻塞模式
              serverSocketChannel.configureBlocking(false);
              //注册到selector，并且设置为连接已建立
              serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
          }
          catch (IOException e) {
              e.printStackTrace();
          }
      }
  
      public static void main(String[] args) {
          GroupChatServer groupChatServer = new GroupChatServer();
          groupChatServer.listen();
      }
  
      public void listen() {
          System.out.println("listen...");
          try {
              while (true) {
                  //得到选择器的数量
                  int select = selector.select();
                  if (select > 0) {
                      Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                      while (iterator.hasNext()) {
                          //获取selectionKey
                          SelectionKey key = iterator.next();
                          //判断是否监听到了accept
                          if (key.isAcceptable()) {
                              SocketChannel socketChannel = serverSocketChannel.accept();
                              socketChannel.configureBlocking(false);
                              //注册到selector，并且设置为读操作
                              socketChannel.register(selector, SelectionKey.OP_READ);
                              System.out.println(socketChannel.getRemoteAddress() + "上线了。。。");
                          }
                          //判断通道是否可读，如果可读就调用读数据方法
                          if (key.isReadable()) {
                              readData(key);
                          }
                          //处理完就删掉，防止重复处理
                          iterator.remove();
                      }
                  }
                  else {
                      //没有选择器的情况下就等待客户端
                      System.out.println("等客户端上线。。。");
                  }
              }
          }
          catch (IOException e) {
              e.printStackTrace();
          }
  
      }
  
      /**
       * 从客户端读取消息
       *
       * @param key
       */
      public void readData(SelectionKey key) {
  
          SocketChannel socketChannel = null;
          try {
              //得到与之关联的通道
              socketChannel = (SocketChannel) key.channel();
              ByteBuffer buffer = ByteBuffer.allocate(1024);
              //将通道中的数据读入buffer
              int read = socketChannel.read(buffer);
              if (read > 0) {
                  String msg = new String(buffer.array(), 0, read);
                  System.out.println("from 客户端： " + msg);
  
                  //向其他客户端转发消息
                  sendInfo2OtherClients(msg, socketChannel);
              }
  
          }
          catch (IOException e) {
              //            e.printStackTrace();
              try {
                  System.out.println(socketChannel.getRemoteAddress() + "离线了。。。");
                  //取消注册
                  key.cancel();
                  //关闭通道
                  socketChannel.close();
              }
              catch (IOException ioException) {
                  ioException.printStackTrace();
              }
          }
  
      }
  
      /**
       * 转发消息给其他客户（通道）
       *
       * @param msg
       * @param socketChannel
       */
      public void sendInfo2OtherClients(String msg, SocketChannel socketChannel) throws IOException {
          System.out.println("sendInfo2OtherClients 服务器转发消息中。。。");
  
          for (SelectionKey k : selector.keys()) {//通过keys取出对应的SocketChannel
              SelectableChannel targetChannel = k.channel();
              //排除自己
              if (targetChannel instanceof SocketChannel && targetChannel != socketChannel) {
                  SocketChannel destChannel = (SocketChannel) targetChannel;
                  //将msg存到buffer
                  ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8));
                  //将msg写到目标用户
                  destChannel.write(buffer);
              }
          }
      }
  
  }
  
  ```

- 客户端代码

  ```java
  package com.nic.nio.groupchat;
  
  import java.io.IOException;
  import java.net.InetSocketAddress;
  import java.nio.ByteBuffer;
  import java.nio.channels.SelectionKey;
  import java.nio.channels.Selector;
  import java.nio.channels.SocketChannel;
  import java.nio.charset.StandardCharsets;
  import java.util.Iterator;
  import java.util.Scanner;
  
  /**
   * Description:
   *
   * @author james
   * @date 2021/7/20 8:28
   */
  public class GroupChatClient
  {
      public static final String HOST = "127.0.0.1";
      public static final int PORT = 7000;
      private Selector selector;
      private SocketChannel socketChannel;
      private String username;
  
      public GroupChatClient() throws IOException {
          selector = Selector.open();
          socketChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
          socketChannel.configureBlocking(false);
          //注册到selector，并且设置为读操作
          socketChannel.register(selector, SelectionKey.OP_READ);
          username = socketChannel.getLocalAddress().toString().substring(1);
  
          System.out.println(username + " is ok!");
      }
  
      public static void main(String[] args) throws IOException {
          GroupChatClient groupChatClient = new GroupChatClient();
          //该线程目的是读取其他客户端的消息，循环读取通道中的消息
          new Thread(() -> {
              while (true) {
                  groupChatClient.readInfo();
                  try {
                      Thread.sleep(3000L);
                  }
                  catch (InterruptedException e) {
                      e.printStackTrace();
                  }
              }
          }).start();
          Scanner scanner = new Scanner(System.in);
          while (scanner.hasNextLine()) {
              groupChatClient.sendInfo(scanner.nextLine());
          }
      }
  
      public void sendInfo(String msg) {
          msg = username + " 说：" + msg;
          try {
              socketChannel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
          }
          catch (IOException e) {
              e.printStackTrace();
          }
      }
  
      /**
       * 读信息逻辑与服务端类似
       * 获取选择器key的个数
       * 如果个数大于0，则挨个处理，把key对应的通道取出来，将通道中的信息读到buffer中，然后输出
       * 如果个数不大于0，则等待
       */
      public void readInfo() {
  
          try {
              int select = selector.select();
              if (select > 0) {
                  Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                  while (iterator.hasNext()) {
                      SelectionKey key = iterator.next();
                      if (key.isReadable()) {
                          SocketChannel channel = (SocketChannel) key.channel();
                          ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                          int read = channel.read(byteBuffer);
                          String msg = new String(byteBuffer.array(), 0, read);
                          System.out.println(msg);
                      }
                      iterator.remove();
                  }
              }
              else {
                  System.out.println("无可用通道。。。");
              }
          }
          catch (Exception e) {
              e.printStackTrace();
          }
      }
  }
  
  ```

效果图以及测试步骤如下：

![image-20210720085333440](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/image-20210720085333440.png)

![image-20210720090202602](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/image-20210720090202602.png)

![image-20210720090353992](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/image-20210720090353992.png)

## 5.NIO与零拷贝

### 5.1 基本介绍

1. 零拷贝是网络编程的关键，很多性能优化都离不开零拷贝
2. 在 `Java` 程序中，常用的零拷贝有 `mmap`（内存映射）和 `sendFile`。那么，他们在 `OS` 里，到底是怎么样的一个的设计？我们分析 `mmap` 和 `sendFile` 这两个零拷贝
3. 另外我们看下 `NIO` 中如何使用零拷贝

### 5.2 传统IO

> DMA：`direct memory access` 直接内存拷贝，不使用CPU

- 上下文切换：当用户程序向内核发起系统调用时，CPU将用户进程从用户态切换到内核态；当系统调用返回时，CPU将用户进程从内核态切换回用户态。
- CPU拷贝：由CPU直接处理数据的传送，数据拷贝时会一直占用CPU的资源。
- DMA拷贝：由CPU向DMA磁盘控制器下达指令，让DMA控制器来处理数据的传送，数据传送完毕再把信息反馈给CPU，从而减轻了CPU资源的占有率。

![image-20210720103839205](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/image-20210720103839205.png)

#### 5.2.1 传统IO读操作

1. 用户通过read向内核空间发起系统调用，上下文从用户态切换为内核态
2. CPU利用DMA控制器将数据从硬盘拷贝到内核缓冲区
3. CPU将内核缓冲区中的数据拷贝到用户空间的用户缓冲区
4. 上下文从内核态切换为用户态，read调用执行返回

#### 5.2.1 传统IO写操作

1. 用户通过write向内核空间发起系统调用，上下文从用户态切换为内核态
2. CPU将用户缓冲区的数据拷贝到socket缓冲区
3. CPU利用DMA控制器从socket缓冲区将数据拷贝至网卡进行数据传输
4. 上下文从内核态切换为用户态，write调用执行返回

> 整个读写操作，总共发生了4次上下文切换，2次CPU拷贝，2次DMA拷贝

### 5.3 零拷贝

零拷贝是在传统IO的基础上进行优化，通过**减少拷贝次数和上下文切换次数**来实现

#### 5.3.1 `mmap`（`mmap`+`write`）

![image-20210720110217117](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/image-20210720110217117.png)

1. 用户进程通过map向内核空间发起系统调用，上下文从用户态切换为内核态
2. **将内核缓冲区与用户缓冲区进行内存地址映射**
3. CPU利用DMA控制器将数据从硬盘拷贝至内核缓冲区
4. 上下文从内核态切换为用户态，mmap系统调用执行返回
5. 用户进程通过write向内核空间发起系统调用，上下文从用户态切换为内核态
6. **CPU将内核缓冲区的数据拷贝到socket缓冲区**
7. CPU利用DMA控制器从socket缓冲区将数据拷贝至网卡进行数据传输
8. 上下文从内核态切换为用户态，write调用执行返回

与传统IO相比，**不同的主要是第2步和第6步**，总的来说，减少了一次CPU拷贝

`mmap`的主要用处是提高IO性能，特别是针对大文件。对于小文件，内存映射文件反而会导致碎片空间的浪费，因为内存映射总是要对齐页边界，最小单位4kb，如果有一个5kb的文件，那将会占用8kb内存，浪费了3kb内存。

> 整个读写操作，总共发生了4次上下文切换，1次CPU拷贝，2次DMA拷贝，相较于传统IO，减少了一次CPU拷贝

#### 5.3.2 sendFile

![image-20210720110345479](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/image-20210720110345479.png)

sendFile在Linux内核版本2.1中引入，目的是简化通过网络在两个通道之间进行的数据传输过程。

1. 用户进程通过sendFile向内核空间发起系统调用，上下文从用户态切换为内核态
2. CPU利用DMA控制器将数据从硬盘拷贝至内核缓冲区
3. CPU将内核缓冲区的数据拷贝到socket缓冲区
4. CPU利用DMA控制器从socket缓冲区将数据拷贝至网卡进行数据传输
5. 上下文从内核态切换为用户态，sendFile调用执行返回

与`mmap`优化相比，sendFile少了两次上下文切换，但问题是用户程序不能直接对数据进行修改，而只是单纯的完成了一次数据传输过程。

#### 5.3.3 sendFile + DMA gather copy

![image-20210720111329407](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/image-20210720111329407.png)

Linux内核版本2.4兑sendFile系统调用进行修改，为DMA引入了gather操作，将内核缓冲区中对应的描述信息（内存地址、地址偏移量、数据长度等）记录到相应的socket缓冲区，有DMA根据对应的描述信息批量的从内核缓冲区将数据直接拷贝至网卡，又节省了一次CPU拷贝操作

1. 用户进程通过sendFile向内核空间发起系统调用，上下文从用户态切换为内核态
2. CPU利用DMA控制器将数据从硬盘拷贝至内核缓冲区
3. CPU将内核缓冲区的文件描述符和数据长度等信息拷贝至socket缓冲区
4. 基于已拷贝的文件描述信息，CPU利用DMA控制器的gather/scatter操作批量的将数据从内核缓冲区直接拷贝至网卡进行数据传输
5. 上下文从内核态切换为用户态，sendFile调用执行返回

sendFile + DMA gather copy同样存在用户程序不能兑数据修改的问题，且本身需要硬件支持，它只是用于将数据从文件拷贝至socket套接字上的传输过程。

### 5.4 对比

综合以上几种IO拷贝方式，DMA都是不可少的，下面次CPU拷贝次数、DMA拷贝次数、上下文切换以及系统调用几个方面总结一下上述几种IO拷贝方式的差别

| 拷贝方式                   | CPU拷贝 | DMA拷贝 | 系统调用   | 上下文切换 |
| -------------------------- | ------- | ------- | ---------- | ---------- |
| 传统方式（read+write）     | 2       | 2       | read/write | 4          |
| mmap（mmap+write）         | 1       | 2       | mmap/write | 4          |
| sendFile                   | 1       | 2       | sendFile   | 2          |
| sendFile + DMA gather copy | 0       | 2       | sendFile   | 2          |

### 5.5 实例

实例说明：

1. 使用传统的 `IO` 方法传递一个大文件
2. 使用 `NIO` 零拷贝方式传递（`transferTo`）一个大文件
3. 看看两种传递方式耗时时间分别是多少

测试步骤如下

1. 启动服务端
2. 启动客户端，客户端会读取本地文件，然后传输至服务端，服务端读到字节流即完成操作

传统IO测试图如下

![image-20210720154733562](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/image-20210720154733562.png)

NIO测试图如下

![image-20210720154908403](https://cdn.jsdelivr.net/gh/beiJxx/PicBed@master/img_epoint/image-20210720154908403.png)

经多次测试，结果NIO耗时基本都在450ms左右，传统IO耗时基本在820ms左右，如使用更大文件测试，结果依然如此，NIO测试耗时比传统IO要快得多。

- 传统IO代码如下

    - 客户端

      ```java
      package com.nic.nio.copy;
      
      import java.io.BufferedInputStream;
      import java.io.BufferedOutputStream;
      import java.io.FileInputStream;
      import java.io.IOException;
      import java.io.InputStream;
      import java.io.OutputStream;
      import java.net.Socket;
      
      /**
       * Description:
       * 传统IO文件传输客户端
       *
       * @author james
       * @date 2021/7/20 14:04
       */
      public class OldIOClient
      {
          public static final String FILE = "D:\\download\\Programs\\ideaIU-2021.1.3.exe";
          public static final String HOST = "127.0.0.1";
          public static final int PORT = 7002;
      
          public static void main(String[] args) {
              Socket socket = null;
              InputStream inputStream = null;
              OutputStream outputStream = null;
              long start = 0L;
              int len = -1;
              long sum = len;
              try {
                  socket = new Socket(HOST, PORT);
                  //从socket中获取输入流
                  inputStream = new BufferedInputStream(new FileInputStream(FILE));
                  //创建输出流
                  outputStream = new BufferedOutputStream(socket.getOutputStream());
      
                  byte[] bytes = new byte[1024 * 1024 * 8];
                  start = System.currentTimeMillis();
                  while ((len = inputStream.read(bytes)) != -1) {
                      outputStream.write(bytes, 0, len);
                      sum += len;
                  }
      
              }
              catch (IOException e) {
                  e.printStackTrace();
              }
              finally {
                  try {
                      if (null != outputStream) {
                          outputStream.flush();
                          outputStream.close();
                      }
                      if (null != inputStream) {
                          inputStream.close();
                      }
                      if (null != socket) {
                          socket.close();
                      }
                  }
                  catch (IOException e) {
                      e.printStackTrace();
                  }
                  long end = System.currentTimeMillis();
                  System.out.println("OldIO 发送字节：" + sum + "，耗时：" + (end - start) + " ms");
              }
          }
      }
      ```

    - 服务端

      ```java
      package com.nic.nio.copy;
      
      import java.io.BufferedInputStream;
      import java.io.IOException;
      import java.io.InputStream;
      import java.net.ServerSocket;
      import java.net.Socket;
      
      /**
       * Description:
       * 传统IO文件传输服务端
       *
       * @author james
       * @date 2021/7/20 14:10
       */
      public class OldIOServer
      {
          public static final int PORT = 7002;
      
          public static void main(String[] args) {
              ServerSocket serverSocket = null;
              InputStream inputStream = null;
              try {
                  serverSocket = new ServerSocket(PORT);
                  System.out.println("OldIOServer 已启动。。。");
                  //连接
                  Socket socket = serverSocket.accept();
                  //获取文件流
                  inputStream = new BufferedInputStream(socket.getInputStream());
                  byte[] flush = new byte[1024];
                  long len = 0L;
                  long sum = len;
                  while ((len = inputStream.read(flush)) != -1) {
                      sum += len;
                  }
                  System.out.println("sum=" + sum);
      
              }
              catch (IOException e) {
                  e.printStackTrace();
              }
              finally {
                  try {
                      if (null != inputStream) {
                          inputStream.close();
                      }
                      if (null != serverSocket) {
                          serverSocket.close();
                      }
                  }
                  catch (IOException e) {
                      e.printStackTrace();
                  }
              }
          }
      }
      ```

- NIO代码如下

    - 客户端

      ```
      package com.nic.nio.copy;
      
      import java.io.FileInputStream;
      import java.io.IOException;
      import java.net.InetSocketAddress;
      import java.nio.channels.FileChannel;
      import java.nio.channels.SocketChannel;
      
      /**
       * Description:
       * nio传输文件客户端
       *
       * @author james
       * @date 2021/7/20 13:54
       */
      public class NewIOClient
      {
          public static final String FILE = "D:\\download\\Programs\\ideaIU-2021.1.3.exe";
      
          public static final String HOST = "127.0.0.1";
          public static final int PORT = 7001;
      
          public static void main(String[] args) {
              send();
          }
      
          public static void send() {
              SocketChannel socketChannel = null;
              FileInputStream fileInputStream = null;
              FileChannel inputChannel = null;
              long count = 0L;
              long sum = count;
              long start = System.currentTimeMillis();
              try {
                  socketChannel = SocketChannel.open();
                  socketChannel.connect(new InetSocketAddress(HOST, PORT));
                  fileInputStream = new FileInputStream(FILE);
                  inputChannel = fileInputStream.getChannel();
                  long size = inputChannel.size();
                  long split = 1024 * 1024 * 8;
                  long c = size / split;
                  //window下最大传输8M，将文件分割8M一份
                  for (int i = 0; i <= c; i++) {
                      count = inputChannel.transferTo(split * i, split, socketChannel);
                      sum += count;
                  }
              }
              catch (IOException e) {
                  e.printStackTrace();
              }
              finally {
                  try {
                      if (null != inputChannel) {
                          inputChannel.close();
                      }
                      if (null != fileInputStream) {
                          fileInputStream.close();
                      }
                  }
                  catch (IOException e) {
                      e.printStackTrace();
                  }
                  long end = System.currentTimeMillis();
                  System.out.println("NewIO 发送字节：" + sum + "， 耗时：" + (end - start) + " ms");
              }
          }
      
      }
      ```

    - 服务端

      ```
      package com.nic.nio.copy;
      
      import java.io.IOException;
      import java.net.InetSocketAddress;
      import java.nio.ByteBuffer;
      import java.nio.channels.ServerSocketChannel;
      import java.nio.channels.SocketChannel;
      
      /**
       * Description:
       * nio传输文件服务端
       *
       * @author james
       * @date 2021/7/20 13:50
       */
      public class NewIOServer
      {
          public static final int PORT = 7001;
      
          public static void main(String[] args) {
              listen();
          }
      
          public static void listen() {
              try {
                  //获得一个ServerSocketChannel通道
                  ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
                  //绑定端口
                  serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
                  System.out.println("NewIOServer 已启动。。。");
                  ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
                  while (true) {
                      SocketChannel socketChannel = serverSocketChannel.accept();
                      int readcount = 0;
                      long sum = readcount;
                      while (-1 != readcount) {
                          try {
                              readcount = socketChannel.read(buffer);
                              sum += readcount;
                          }
                          catch (IOException e) {
                              break;
                          }
                          buffer.rewind();
                      }
                      System.out.println("sum=" + sum);
                  }
              }
              catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }
      ```

## AIO

1. `JDK7` 引入了 `AsynchronousI/O`，即 `AIO`。在进行 `I/O` 编程中，常用到两种模式：`Reactor` 和 `Proactor`。`Java` 的 `NIO` 就是 `Reactor`，当有事件触发时，服务器端得到通知，进行相应的处理
2. `AIO` 即 `NIO2.0`，叫做异步不阻塞的 `IO`。`AIO` 引入异步通道的概念，采用了 `Proactor` 模式，简化了程序编写，有效的请求才启动线程，它的特点是先由操作系统完成后才通知服务端程序启动线程去处理，一般适用于连接数较多且连接时间较长的应用
3. 目前 `AIO` 还没有广泛应用，`Netty` 也是基于 `NIO`，而不是 `AIO`，因此我们就不详解 `AIO` 了

## 总结

最后用一张表格总结下各类IO的特性

|          | BIO      | NIO                    | AIO        |
| -------- | -------- | ---------------------- | ---------- |
| IO模型   | 同步阻塞 | 同步非阻塞（多路复用） | 异步非阻塞 |
| 编程难度 | 简单     | 复杂                   | 复杂       |
| 可靠性   | 差       | 好                     | 好         |
| 吞吐量   | 低       | 高                     | 高         |

> 资料参考

https://dongzl.github.io/netty-handbook

https://cloud.tencent.com/developer/article/1786955

https://www.cnblogs.com/lsgxeva/p/11619464.html
