package core.base.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * 专门用于处理IO操作的工具类
 *
 * @author DrkCore
 * @since 2015年9月27日15:35:27
 */
public final class IOUtil {

    private IOUtil() {
    }

	/* 静默关闭IO对象 */

    /**
     * 静默关闭输入流。
     * 如果clostable同时
     *
     * @param closeable
     */
    public static void close(Closeable closeable) {
        if (closeable != null) {
            if (closeable instanceof Flushable) {//如果是可flush的，就先flush一下
                try {
                    Flushable flushable = (Flushable) closeable;
                    flushable.flush();
                } catch (IOException e) {
                    LogUtil.e(e);
                }
            }
            try {
                closeable.close();
            } catch (IOException e) {
                LogUtil.e(e);
            }
        }
    }

	/* 读操作 */

    /**
     * {@link #readString(InputStream, Charset, boolean)}，自动关闭流
     *
     * @param in
     * @param charset
     * @return
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static String readString(InputStream in, Charset charset) throws IOException {
        return readString(in, charset, true);
    }

    /**
     * 将输入流转换成指定编码的字符串。
     * 这里使用{@link #readBytes(InputStream, boolean)}
     * 一次性读取输入流的数据为byte数组，并一次性按编码转化为String，
     * 这两次都会分配新的内存。所以该方法的速度较快但如果读入的数据较大时会对内存造成压力。
     *
     * @param in
     * @param charset
     * @param autoClose
     * @return 输入流中的数据。
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static String readString(InputStream in, Charset charset, boolean autoClose) throws IOException {
        return new String(readBytes(in, autoClose), charset);
    }

    /**
     * {@link #readBytes(InputStream, boolean)}，自动关闭流
     *
     * @param in
     * @return
     * @throws IOException
     */
    public static byte[] readBytes(InputStream in) throws IOException {
        return readBytes(in, true);
    }

    /**
     * 将指定输入流的数据读出为byte数组。
     *
     * @param in
     * @param autoClose
     * @return
     * @throws IOException
     */
    public static byte[] readBytes(InputStream in, boolean autoClose) throws IOException {
        ByteArrayOutputStream byteArrOut = null;
        try {
            byteArrOut = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                byteArrOut.write(buffer, 0, len);
            }
            byteArrOut.flush();
            return byteArrOut.toByteArray();
        } finally {
            if (autoClose) {
                close(byteArrOut);
                close(in);
            }
        }
    }

	/* 写操作 */

    /**
     * 具体实现请参阅{@link #writeString(OutputStream, String, Charset)}，默认使用系统编码
     */
    public static void writeString(OutputStream out, String content) throws IOException {
        writeString(out, content, Charset.defaultCharset());
    }

    /**
     * 具体实现请参阅 {@link #writeString(OutputStream, String, Charset, boolean)}，自动关闭流
     */
    public static void writeString(OutputStream out, String content, Charset charset) throws IOException {
        writeString(out, content, charset, true);
    }

    /**
     * 使用指定编码向输出流写入文字
     *
     * @param out
     * @param content
     * @param charset
     * @param autoClose 写入后是否自动关闭输出流
     * @throws IOException
     */
    public static void writeString(OutputStream out, String content, Charset charset, boolean autoClose) throws IOException {
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(out, charset);
            writer.write(content);
            writer.flush();
        } finally {
            if (autoClose) {
                close(writer);
                close(out);
            }
        }
    }

    /**
     * 具体实现请参阅{@link #writeData(InputStream, OutputStream, boolean)}，默认自动关闭流。
     */
    public static void writeData(InputStream in, OutputStream out) throws IOException {
        writeData(in, out, true);
    }

    /**
     * 将输入流的数据写入输出流。
     *
     * @param in
     * @param out
     * @param autoClose 写入后是否关闭流
     * @throws IOException
     */
    public static void writeData(InputStream in, OutputStream out, boolean autoClose) throws IOException {
        // 创建临时变量准备输入
        byte[] data = new byte[1024];
        int len;
        // 将数据写入指定的文件
        try {
            while ((len = in.read(data)) != -1) {
                out.write(data, 0, len);
            }
        } finally {
            if (autoClose) {
                close(in);
                close(out);
            }
        }
    }
}
