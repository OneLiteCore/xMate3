package core.base.util;

import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 封装获取数据摘要的方法的工具类。
 *
 * @author DrkCore
 * @since 2015年9月28日00:36:06
 */
public class DigestUtil {

    private DigestUtil() {
    }

	/* 获取摘要 */

    /**
     * 使用{@link Charset#defaultCharset()}默认编码，在Android上也就是UTF-8获取字符串的字节数组，
     * 再计算其摘要。摘要默认为小写。
     *
     * @param algorithm
     * @param str
     * @return
     */
    public static String digest(String algorithm, String str) {
        return digest(algorithm, str, null);
    }

    /**
     * 按照指定编码获取字符串的字节数组，并计算摘要。摘要默认为小写。
     * 如果charset为null则使用默认编码。
     *
     * @param algorithm
     * @param str
     * @param charset
     * @return
     */
    public static String digest(String algorithm, String str, @Nullable Charset charset) {
        return digest(algorithm, str.getBytes(charset != null ? charset : Charset.defaultCharset()));
    }

    /**
     * 获取字节数组的摘要。摘要默认为小写。
     *
     * @param algorithm
     * @param bytes
     * @return
     */
    public static String digest(String algorithm, byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            digest.reset();
            return EncodeUtil.toHexString(digest.digest(bytes));
        } catch (NoSuchAlgorithmException e) {
            LogUtil.e(e);
            throw new IllegalStateException("无法初始化" + algorithm + "算法");
        }
    }

    /**
     * 获取文件摘要，摘要默认为小写
     *
     * @param algorithm
     * @param file
     * @return
     * @throws IOException
     */
    public static String digest(String algorithm, File file) throws IOException {
        return digest(algorithm, new FileInputStream(file));
    }

    /**
     * 获取输入流的摘要。摘要默认为小写。
     *
     * @param algorithm
     * @param in
     * @return
     * @throws IOException
     */
    public static String digest(String algorithm, InputStream in) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            digest.reset();
            byte[] buff = new byte[1024];
            int len;
            while ((len = (in.read(buff))) != -1) {
                digest.update(buff, 0, len);
            }
            return EncodeUtil.toHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            LogUtil.e(e);
            throw new IllegalStateException("无法初始化" + algorithm + "算法");
        } finally {
            IOUtil.close(in);
        }
    }

	/* 简化方法 */

    public static final String ALGORITHM_MD5 = "MD5";
    public static final String ALGORITHM_SHA = "SHA";

    /**
     * 获取字符串的16为md5的摘要。默认小写。
     * 具体实现请参阅{@link #digestMD5(String)}。
     *
     * @param str
     * @return
     */
    public static String digestSimpleMD5(String str) {
        String md5 = digestMD5(str);
        return md5.substring(8, 24);
    }

    /**
     * 获取字符串的32位md5摘要，摘要默认为小写。 具体实现请参阅{@link #digest(String, String)}。
     *
     * @param str
     * @return
     */
    public static String digestMD5(String str) {
        return digest(ALGORITHM_MD5, str);
    }

    /**
     * 获取字符串的sha摘要，摘要默认为小写。 具体实现请参阅{@link #digest(String, String)}。
     *
     * @param str
     * @return
     */
    public static String digestSHA(String str) {
        return digest(ALGORITHM_SHA, str);
    }

}
