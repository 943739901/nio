package com.lpy.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 一、使用 NIO 完成网络通信的三个核心
 *
 * 1. 通道（Channel）：负责连接
 *
 *      java.nio.channels.Channel 接口：
 *          |--SelectableChannel
 *              |--SocketChannek
 *              |--ServerSocketChannel
 *              |--DataGramChannel
 *
 *              |--Pipe.SinkChannel
 *              |--Pipe.SourceChannel
 *
 * 2. 缓冲区（Buffer）：负责数据的存取
 *
 * 3. 选择器（Selector）：是 SelectableChannel 的多路复用器。用于监控 SelectableChannel 的 IO 状况
 *
 * @author lipengyu
 * @date 2019/9/2 9:28
 */
public class TestNonBlockingNIO{

}
