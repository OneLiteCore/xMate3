package core.base.util;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import core.base.text.AbsCharFilter;
import core.base.text.LineSeparator;
import core.base.text.NotCharFilter;

/**
 * 处理文本文件读写等操作的工具类
 *
 * @author DrkCore
 * @since 2015年9月26日18:09:35
 */
public final class TextUtil {

    private TextUtil() {
    }

	/* 文本长度 */

    public static long countTextLength(File txtFile) throws IOException {
        return countTextLength(txtFile, null, null);
    }

    public static long countTextLength(File txtFile, @Nullable AbsCharFilter filter) throws IOException {
        return countTextLength(txtFile, null, filter);
    }

    /**
     * 获取文本文件内容的长度。
     *
     * @param txtFile
     * @param charset
     * @param filter  字符过滤，为null表示不使用过滤规则。
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static long countTextLength(File txtFile, @Nullable Charset charset, @Nullable AbsCharFilter filter) throws IOException {
        if (!txtFile.isFile()) {
            throw new FileNotFoundException("无法读取非文本文件");
        } else if (txtFile.length() == 0) {
            return 0;
        }

        BufferedReader reader = null;
        try {
            long count = 0;
            // 读取并计数
            charset = charset != null ? charset : Charset.defaultCharset();
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(txtFile), charset));
            char[] buff = new char[1024];
            int len;
            if (filter != null) {
                while ((len = reader.read(buff)) != -1) {
                    count += filter.count(buff, 0, len);
                }
            } else {
                while ((len = reader.read(buff)) != -1) {
                    count += len;
                }
            }
            return count;
        } finally {
            IOUtil.close(reader);
        }
    }

	/* 读取 */

    public static String readText(File txtFile) throws IOException {
        return readText(txtFile, null, null);
    }

    /**
     * 读取指定文件中的文本。当指定文本编码为null时使用默认编码，当换行符为null时使用系统默认换行符。
     *
     * @param txtFile
     * @param charset       指定的文本编码，
     * @param lineSeparator 换行符
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String readText(File txtFile, @Nullable Charset charset, @Nullable LineSeparator lineSeparator) throws IOException {
        if (!txtFile.isFile()) {// 不是文件
            throw new FileNotFoundException("文件不可用");
        } else if (txtFile.length() == 0) {
            return "";// 空文件直接返回
        }

        InputStream in = null;
        InputStreamReader inputReader = null;
        BufferedReader buffReader = null;
        try {
            StringBuilder builder = new StringBuilder();
            in = new FileInputStream(txtFile);
            charset = charset != null ? charset : Charset.defaultCharset();
            inputReader = new InputStreamReader(in, charset);
            buffReader = new BufferedReader(inputReader);

            // 读取文本
            String temp;
            lineSeparator = lineSeparator != null ? lineSeparator : LineSeparator.SYS;
            String separator = lineSeparator.toString();
            while ((temp = buffReader.readLine()) != null) {
                builder.append(temp).append(separator);
            }
            if (builder.length() > separator.length()) {// 有长度则必定末尾有一个多余的\n符
                builder.deleteCharAt(builder.length() - separator.length());
            }
            return builder.toString();
        } finally {
            IOUtil.close(buffReader);
            IOUtil.close(inputReader);
            IOUtil.close(in);
        }

    }

    public static String readLength(File txtFile, int len) throws IOException {
        return readLength(txtFile, null, len);
    }

    /**
     * 从文本文件中读取指定长度的字符串。
     * 如果指定编码为null则使用默认编码。
     *
     * @param txtFile
     * @param charset
     * @param len
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String readLength(File txtFile, @Nullable Charset charset, int len) throws IOException {
        if (!txtFile.isFile()) {
            throw new FileNotFoundException("指定文本文件不可用");
        } else if (len <= 0) {
            throw new IllegalArgumentException("len必须大于零");
        }

        FileInputStream in = null;
        InputStreamReader inReader = null;
        BufferedReader buffReader = null;
        try {
            // 初始化输入
            in = new FileInputStream(txtFile);
            charset = charset != null ? charset : Charset.defaultCharset();
            inReader = new InputStreamReader(in, charset);
            buffReader = new BufferedReader(inReader);
            // 因为读取的文本最长长度已知，这里直接设置StringBuilder的长度
            StringBuilder builder = new StringBuilder(len);
            char[] buff = new char[128];// 128长度是足够的
            int bufferedLen, i, sum = 0;
            char ch;
            AbsCharFilter charFilter = new NotCharFilter(NotCharFilter.SAMPLE_EMPTY);
            mainLoop:
            while ((bufferedLen = buffReader.read(buff)) > 0) {
                for (i = 0; i < bufferedLen; i++) {
                    ch = buff[i];
                    if (charFilter.accept(ch)) {
                        builder.append(ch);

                        // 检查是否读取完毕
                        if (++sum >= len) {
                            break mainLoop;// 跳出多层循环
                        }
                    }
                }
            }
            return builder.toString();
        } finally {
            IOUtil.close(buffReader);
            IOUtil.close(inReader);
            IOUtil.close(in);
        }
    }

    public static List<String> readLines(File txtFile) throws IOException {
        return readLines(txtFile, null);
    }

    /**
     * 分行读取输入流。如果charset为null则使用默认编码。
     *
     * @param txtFile
     * @param charset
     * @return
     * @throws IOException
     */
    public static List<String> readLines(File txtFile, @Nullable Charset charset) throws IOException {
        InputStream in = null;
        InputStreamReader inReader = null;
        BufferedReader buffReader = null;
        try {
            in = new FileInputStream(txtFile);
            charset = charset != null ? charset : Charset.defaultCharset();
            inReader = new InputStreamReader(in, charset);
            buffReader = new BufferedReader(inReader);
            ArrayList<String> list = new ArrayList<>();
            String line;
            while ((line = buffReader.readLine()) != null) {
                list.add(line);
            }
            return list;
        } finally {
            IOUtil.close(buffReader);
            IOUtil.close(inReader);
            IOUtil.close(in);
        }
    }

	/* 写入 */

    public static void writeText(File txtFile, String content) throws IOException {
        writeText(txtFile, content, null);
    }

    /**
     * 将字符串按照编码保存到指定文本文件中。
     * 写入的实现请参照 {@link IOUtil#writeString(OutputStream, String, Charset)}
     *
     * @param txtFile 需要保存到的文件。如果该文件不存在则会通过 {@link FileUtil#createFile(File)}尝试创建。
     * @param content 需要保存的字符串
     * @param charset 文本编码。为null表示使用系统默认编码。
     * @throws UnsupportedEncodingException
     * @throws FileNotFoundException        文件无法创建，或者路径被目录占用
     * @throws IOException
     */
    public static void writeText(File txtFile, String content, @Nullable Charset charset) throws IOException {
        charset = charset != null ? charset : Charset.defaultCharset();

        if (!txtFile.exists()) {// 文件不存在
            if (!FileUtil.createFile(txtFile).isFile()) {// 文件不存在且创建文件失败
                throw new FileNotFoundException("无法创建文件");
            }
        } else if (txtFile.isDirectory()) {// 目录
            throw new FileNotFoundException("无法编辑目录");
        }

        // 写入文本
        IOUtil.writeString(new FileOutputStream(txtFile), content, charset);
    }

	/*其他*/

    public static boolean isEmpty(CharSequence chars) {
        return TextUtils.isEmpty(chars);
    }

    public static boolean isAllEmpty(String... strs) {
        if (strs != null) {
            for (String tmp : strs) {
                if (!isEmpty(tmp)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isAnyEmpty(String... strs) {
        if (strs != null) {
            for (String tmp : strs) {
                if (isEmpty(tmp)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public static String getEmptyIfNull(String str) {
        return str != null ? str : "";
    }

    public static CharSequence getEmptyIfNull(CharSequence str) {
        return str != null ? str : "";
    }

    /**
     * 获取字符串的下标从0到length的子串。如果str的长度不足length则直接返回str。
     *
     * @param str
     * @param length
     * @return
     */
    public static String subString(String str, int length) {
        int srcLen = str.length();
        if (srcLen <= length) {
            return str;
        } else {
            return str.substring(0, length);
        }
    }

    public static String buildString(Object... objs) {
        return buildCharSequence(objs).toString();
    }

    public static CharSequence buildCharSequence(Object... objs) {
        int len = objs.length;
        if (len == 0) {
            return "";
        }
        String[] strs = new String[len];
        while (--len >= 0) {
            strs[len] = objs[len].toString();
        }
        return TextUtils.concat(strs);
    }

    public static String[] toString(CharSequence... charSequences) {
        if (charSequences == null) {
            return null;
        }

        int i = charSequences.length;
        String[] strs = new String[i];
        while (--i >= 0) {
            strs[i] = charSequences[i] != null ? charSequences[i].toString() : null;
        }
        return strs;
    }
}
