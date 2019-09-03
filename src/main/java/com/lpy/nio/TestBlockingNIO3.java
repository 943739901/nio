package com.lpy.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 *
 * 1. shutdownOutput()单向关闭输出流后，如果再次开启？
 * 2. 除了shutdownOutput()，有什么办法通知xx端已经读 或者 写 完了
 * 3. 为什么会阻塞？？
 *
 *
 * 如果每次读取 或者 写入 都创建新的 channel 应该就没问题了
 *
 *
 * @author lipengyu
 * @date 2019/9/2 9:29
 */
public class TestBlockingNIO3 {

    //客户端
    @Test
    public void client() throws IOException {
        SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));

        FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("3.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        FileChannel outChannel1 = FileChannel.open(Paths.get("3.jpg"), StandardOpenOption.READ);

        ByteBuffer buf = ByteBuffer.allocate(1024);

        while (inChannel.read(buf) != -1) {
            buf.flip();
            sChannel.write(buf);
            buf.clear();
        }

        // 如果不shutdown服务端不知道是否已经读完，在客户端想 read 时就会阻塞
//        sChannel.shutdownOutput();

        while (sChannel.read(buf) != -1) {
            buf.flip();
            outChannel.write(buf);
            buf.clear();
        }

        SocketChannel sChannel1 = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));

        while (outChannel1.read(buf) != -1) {
            buf.flip();
            sChannel1.write(buf);
            buf.clear();
        }

        inChannel.close();
        sChannel.close();
    }

    //服务端
    @Test
    public void server() throws IOException {
        ServerSocketChannel ssChannel = ServerSocketChannel.open();

        FileChannel outChannel = FileChannel.open(Paths.get("2.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        FileChannel outChannel1 = FileChannel.open(Paths.get("2.jpg"), StandardOpenOption.READ);
        FileChannel inChannel = FileChannel.open(Paths.get("4.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        ssChannel.bind(new InetSocketAddress(9898));

        SocketChannel sChannel = ssChannel.accept();

        ByteBuffer buf = ByteBuffer.allocate(1024);

        while (sChannel.read(buf) != -1) {
            buf.flip();
            outChannel.write(buf);
            buf.clear();
        }

        //发送反馈给客户端
//
//        buf.put("服务端接受数据成功".getBytes());
//        buf.flip();
//        sChannel.write(buf);
//        buf.clear();

        while (outChannel1.read(buf) != -1) {
            buf.flip();
            sChannel.write(buf);
            buf.clear();
        }

        sChannel.shutdownOutput();
//
        sChannel = ssChannel.accept();

        while (sChannel.read(buf) != -1) {
            buf.flip();
            inChannel.write(buf);
            buf.clear();
        }

        sChannel.close();
        outChannel.close();
        ssChannel.close();
    }
}
