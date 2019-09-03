package com.lpy.nio;

import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.FileAttribute;
import java.util.Date;

/**
 * @author lipengyu
 * @date 2019/9/3 14:29
 */
public class TestNIO_2 {


    /**
     * 自动资源管理：自动关闭实现 AutoCloseable 接口的资源
     */
    @Test
    public void test8() {
        try(FileChannel incChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
                FileChannel outChannel = FileChannel.open(Paths.get("2.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            ByteBuffer buf = ByteBuffer.allocate(1024);
            incChannel.read(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Files常用方法：用于操作内容
     * @see Files#newByteChannel(Path, OpenOption...) : 获取与指定文件的连接，how 指定打开方式。
     * @see Files#newDirectoryStream(Path) : 打开 path 指定的目录
     * @see Files#newInputStream(Path, OpenOption...) : 获取 InputStream 对象
     * @see Files#newOutputStream(Path, OpenOption...) : 获取 OutputStream 对象
     *
     * @throws IOException
     */
    @Test
    public void test7() throws IOException {
        SeekableByteChannel newByteChannel = Files.newByteChannel(Paths.get("1.jpg"), StandardOpenOption.READ);

        DirectoryStream<Path> newDirectoryStream = Files.newDirectoryStream(Paths.get("e:/"));

        for (Path path : newDirectoryStream) {
            System.out.println(path);
        }
    }


    /**
     * Files常用方法：用于判断
     * @see Files#exists(Path, LinkOption...) : 判断文件是否存在
     * @see Files#isDirectory(Path, LinkOption...) : 判断是否是目录
     * @see Files#isExecutable(Path) : 判断是否为可执行文件
     * @see Files#isHidden(Path) : 判断是否为隐藏文件
     * @see Files#isReadable(Path) : 判断是否文件可读
     * @see Files#isWritable(Path) : 判断文件是否可写
     * @see Files#notExists(Path, LinkOption...) : 判断文件是否不存在
     * @see Files#readAttributes(Path, Class, LinkOption...) : 获取与 path 指定的文件相关联的属性。
     *
     * @throws IOException
     */
    @Test
    public void test6() throws IOException {
        Path path = Paths.get("e:/nio/hello7.txt");
        System.out.println(Files.exists(path, LinkOption.NOFOLLOW_LINKS));

        BasicFileAttributes readAttributes = Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
        System.out.println(new Date(readAttributes.creationTime().toMillis()));
        System.out.println(new Date(readAttributes.lastModifiedTime().toMillis()));

        DosFileAttributeView fileAttributeView = Files.getFileAttributeView(path, DosFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);

        fileAttributeView.setHidden(false);
    }


    /**
     * Files常用方法
     * @see Files#copy(Path, Path, java.nio.file.CopyOption...) ：文件的复制
     * @see Files#createDirectory(Path, FileAttribute[]) ：创建一个目录
     * @see Files#createFile(Path, FileAttribute[]) ：创建一个文件
     * @see Files#delete(Path) : 删除一个文件
     * @see Files#move(Path, Path, CopyOption...) : 将 src 移动到 dest 位置
     * @see Files#size(Path) : 返回 path 指定文件的大小
     *
     * @throws IOException
     */
    @Test
    public void test5() throws IOException {
        Path path1 = Paths.get("e:/nio/hello2.txt");
        Path path2 = Paths.get("e:/nio/hello7.txt");

        System.out.println(Files.size(path2));

//        Files.move(path1, path2, StandardCopyOption.ATOMIC_MOVE);
    }

    @Test
    public void test4() throws IOException {
        Path dir = Paths.get("e:/nio/nio2");
        Files.createDirectory(dir);

        Path file = Paths.get("e:/nio/nio2/hello3.txt");
        Files.createFile(file);

        Files.deleteIfExists(file);
    }


    @Test
    public void test3() throws IOException {
        Path path1 = Paths.get("e:/nio/hello.txt");
        Path path2 = Paths.get("e:/nio/hello2.txt");

        Files.copy(path1, path2, StandardCopyOption.REPLACE_EXISTING);
    }


    /**
     * Paths 提供的 get() 方法用来获取 Path 对象：
     * @see Paths#get(String, String...) ：用于将多个字符串串连成路径。
     * Path 常用方法
     * @see Path#endsWith(String) ：判断是否以 path 路径结束
     * @see Path#startsWith(String) ：判断是否以 path 路径开始
     * @see Path#isAbsolute() ：判断是否是绝对路径
     * @see Path#getFileName() ：返回与调用 Path 对象关联的文件名
     * @see Path#getName(int) ：返回的指定索引位置 idx 的路径名称
     * @see Path#getNameCount() ：返回Path 根目录后面元素的数量
     * @see Path#getParent() ：返回Path对象包含整个路径，不包含 Path 对象指定的文件路径
     * @see Path#getRoot() ：返回调用 Path 对象的根路径
     * @see Path#resolve(Path) ：将相对路径解析为绝对路径
     * @see Path#toAbsolutePath() ：作为绝对路径返回调用 Path 对象
     * @see Path#toString() ：返回调用 Path 对象的字符串表示形式
     */
    @Test
    public void test2() {
        Path path = Paths.get("e:/nio/hello.txt");

        System.out.println(path.getParent());
        System.out.println(path.getRoot());

        Path newPath = path.resolve("e:/hello.txt");
        System.out.println(newPath);

        Path path2 = Paths.get("1.jpg");
        Path newPath2 = path2.toAbsolutePath();
        System.out.println(newPath2);

        System.out.println(path.toString());
    }

    @Test
    public void test1() {
        Path path = Paths.get("e:/", "nio/hello.txt");

        System.out.println(path.endsWith("hello.txt"));
        System.out.println(path.startsWith("e:/"));

        System.out.println(path.isAbsolute());
        System.out.println(path.getFileName());

        for (int i = 0; i < path.getNameCount(); i++) {
            System.out.println(path.getName(i));
        }
    }
}
