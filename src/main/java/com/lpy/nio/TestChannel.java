package com.lpy.nio;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.util.SortedMap;

/**
 * 一、通道（Channel）：用于源节点与目标节点的连接，在 Java NIO 中负责缓冲区中数据的传输。Channel 本身不存储数据，一次需要配合缓冲区进行传输。
 *
 * 二、通道的主要实现类
 *  java.nio.channels.Channel接口
 *      |--FileChannel
 *      |--SocketChannel
 *      |--ServerSockerChannel
 *      |--DatagramChannel
 *
 * 三、获取通道
 * 1. Java 针对支持通道的类提供了 getChannel() 方法
 *      本地 IO：
 *      FileInputStream/FileOutputStream
 *      RancomAcessFile
 *
 *      网络IO：
 *      Socket
 *      ServerSocket
 *      DatagramSocket
 *
 * 2. 在 JDK 1.7 中的 NIO.2 针对各个通道提供了静态方法 open()
 * 3. 在 JDK 1.7 中的 NIO.2 的 Files 工具类的 newByteChannel()
 *
 * 四、通道之间的数据传输
 * transferForm()
 * transferTo()
 *
 * 五、分散（Scatter）与聚集（Gather）
 * 分散读取（Scattering Reads）：将通道中的数据分散到多个缓冲区中
 * 聚集写入（Gathering Writes）：将多个缓冲区中的数据聚集到通道中
 *
 * 六、字符集：Charset
 * 编码：字符串 -> 字节数组
 * 解码：字节数组 -> 字符串
 *
 * @author lipengyu
 * @date 2019/8/30 17:01
 */
public class TestChannel {

    //字符集
    @Test
    public void test6() throws IOException {
        Charset cs1 = Charset.forName("GBK");

        //获取编码器
        CharsetEncoder ce = cs1.newEncoder();

        //获取解码器
        CharsetDecoder cd = cs1.newDecoder();

        CharBuffer cBuf = CharBuffer.allocate(1024);
        cBuf.put("我是李鹏宇！");
        cBuf.flip();

        //编码
        ByteBuffer bBuf = ce.encode(cBuf);

        for (int i = 0; i < 12; i++) {
            System.out.println(bBuf.get());
        }

        //解码
        bBuf.flip();
        CharBuffer cBuf2 = cd.decode(bBuf);
        System.out.println(cBuf2.toString());

        System.out.println("-------------------------------------------");

        Charset cs2 = Charset.forName("GBK");
        bBuf.flip();
        CharBuffer cBuf3 = cs2.decode(bBuf);
        System.out.println(cBuf3.toString());
    }


    @Test
    public void test5() {
        SortedMap<String, Charset> map = Charset.availableCharsets();
        map.forEach((x, y) -> System.out.println(x + "=" + y));
    }


    //分散和聚集
    @Test
    public void test4() throws IOException {
            RandomAccessFile raf1 = new RandomAccessFile("1.txt", "rw");

            //1. 获取通道
            FileChannel channel1 = raf1.getChannel();

            //2. 分配指定大小的缓冲区
            ByteBuffer buf1 = ByteBuffer.allocate(100);
            ByteBuffer buf2 = ByteBuffer.allocate(1024);

            //3. 分散读取
            ByteBuffer[] bufs = {buf1, buf2};
            channel1.read(bufs);

            for (ByteBuffer byteBuffer : bufs) {
                byteBuffer.flip();
            }

            System.out.println(new String(bufs[0].array(), 0, bufs[0].limit()));
            System.out.println("-----------------------");
            System.out.println(new String(bufs[1].array(), 0, bufs[1].limit()));

            //4. 聚集写入
            RandomAccessFile raf2 = new RandomAccessFile("2.txt","rw");
            FileChannel channel2 = raf2.getChannel();

            channel2.write(bufs);
    }




    /**
     * 通道之间的数据传输（直接缓冲区）
     *
     * 762 - 758 - 797
     *
     * @throws IOException
     */
    @Test
    public void test3() throws IOException {
        Instant start = Instant.now();

        FileChannel inChannel = FileChannel.open(Paths.get("d:/1.mp4"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("d:/2.mp4"), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

//        inChannel.transferTo(0, inChannel.size(), outChannel);
        outChannel.transferFrom(inChannel, 0, inChannel.size());

        inChannel.close();
        outChannel.close();

        Instant end = Instant.now();
        System.out.println("耗费时间：" + Duration.between(start, end).toMillis());
    }


    /**
     * 使用直接缓冲区完成文件的复制（内存映射文件）
     *
     * 1711 - 1401 - 1425
     *
     * 虽然效率更高，但是可能会任务完成，内存不会被回收，造成长时间内存占用高的问题
     * @throws IOException
     */
    @Test
    public void test2() throws IOException {

        Instant start = Instant.now();

        FileChannel inChannel = FileChannel.open(Paths.get("d:/1.mp4"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("d:/2.mp4"), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        //内存映射文件
        MappedByteBuffer inMappedBuf = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        MappedByteBuffer outMappedBuf = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());

        //直接对缓冲区进行数据的读写操作
        byte[] dst = new byte[inMappedBuf.limit()];
        inMappedBuf.get(dst);
        outMappedBuf.put(dst);

        inChannel.close();
        outChannel.close();

        Instant end = Instant.now();
        System.out.println("耗费时间：" + Duration.between(start, end).toMillis());
    }



    /**
     * 利用通道完成文件的复制（非直接缓冲区）
     * 7163 - 6941 - 6776
     */
    @Test
    public void test1() {

        Instant start = Instant.now();

        FileInputStream fis = null;
        FileOutputStream fos = null;

        //①获取通道
        FileChannel inChannel = null;
        FileChannel outChannel = null;

        try {
            fis = new FileInputStream("d:/1.mp4");
            fos = new FileOutputStream("d:/2.mp4");

            inChannel = fis.getChannel();
            outChannel = fos.getChannel();

            //②分配指定大小的缓冲区
            ByteBuffer buf = ByteBuffer.allocate(1024);

            //③将通道中的数据存入缓冲区中
            while (inChannel.read(buf) != -1) {
                //切换读取数据的模式
                buf.flip();
                //④将缓冲区中的数据写入通道中
                outChannel.write(buf);
                //情况缓冲区
                buf.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outChannel != null) {
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inChannel != null) {
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Instant end = Instant.now();
        System.out.println("耗费时间：" + Duration.between(start, end).toMillis());
    }
}






















































