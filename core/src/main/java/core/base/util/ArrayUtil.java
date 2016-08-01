package core.base.util;

import android.support.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 用于数组操作的工具集
 *
 * @author DrkCore
 * @since 2016年2月17日17:43:03
 */
public final class ArrayUtil {

    private ArrayUtil() {
    }

	/*数组操作*/

    public static void reverse(byte[] array) {
        int len = array.length;
        if (len <= 1) {
            return;
        }

        int halfLen = len / 2;
        byte tmp;
        for (int i = 0; i < halfLen; i++) {
            tmp = array[i];
            array[i] = array[len - i - 1];
            array[len - i - 1] = tmp;
        }
    }

    public static void reverse(boolean[] array) {
        int len = array.length;
        if (len <= 1) {
            return;
        }

        int halfLen = len / 2;
        boolean tmp;
        for (int i = 0; i < halfLen; i++) {
            tmp = array[i];
            array[i] = array[len - i - 1];
            array[len - i - 1] = tmp;
        }
    }

    public static void reverse(char[] array) {
        int len = array.length;
        if (len <= 1) {
            return;
        }

        int halfLen = len / 2;
        char tmp;
        for (int i = 0; i < halfLen; i++) {
            tmp = array[i];
            array[i] = array[len - i - 1];
            array[len - i - 1] = tmp;
        }
    }

    public static void reverse(int[] array) {
        int len = array.length;
        if (len <= 1) {
            return;
        }

        int halfLen = len / 2;
        int tmp;
        for (int i = 0; i < halfLen; i++) {
            tmp = array[i];
            array[i] = array[len - i - 1];
            array[len - i - 1] = tmp;
        }
    }

    public static void reverse(long[] array) {
        int len = array.length;
        if (len <= 1) {
            return;
        }

        int halfLen = len / 2;
        long tmp;
        for (int i = 0; i < halfLen; i++) {
            tmp = array[i];
            array[i] = array[len - i - 1];
            array[len - i - 1] = tmp;
        }
    }

    public static void reverse(float[] array) {
        int len = array.length;
        if (len <= 1) {
            return;
        }

        int halfLen = len / 2;
        float tmp;
        for (int i = 0; i < halfLen; i++) {
            tmp = array[i];
            array[i] = array[len - i - 1];
            array[len - i - 1] = tmp;
        }
    }

    public static void reverse(double[] array) {
        int len = array.length;
        if (len <= 1) {
            return;
        }

        int halfLen = len / 2;
        double tmp;
        for (int i = 0; i < halfLen; i++) {
            tmp = array[i];
            array[i] = array[len - i - 1];
            array[len - i - 1] = tmp;
        }
    }

    public static <T> void reverse(T[] array) {
        int len = array.length;
        if (len <= 1) {
            return;
        }

        int halfLen = len / 2;
        T tmp;
        for (int i = 0; i < halfLen; i++) {
            tmp = array[i];
            array[i] = array[len - i - 1];
            array[len - i - 1] = tmp;
        }
    }

	/*判断/获取*/

    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 当array为null，或者长度为0时返回true。
     *
     * @param array
     * @param <T>
     * @return
     */
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(boolean[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(char[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(long[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(float[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(double[] array) {
        return array == null || array.length == 0;
    }

    @Nullable
    public static <T> T getQuietly(@Nullable List<T> list, int location) {
        if (location < 0) {
            throw new IllegalArgumentException("location不允许为负数");
        }
        return list != null && list.size() > location ? list.get(location) : null;
    }

    @Nullable
    public static <T> T getFirstQuietly(@Nullable List<T> list) {
        return getQuietly(list, 0);
    }

    @Nullable
    public static <T> T getLastQuietly(@Nullable List<T> list) {
        return getQuietly(list, list != null ? list.size() - 1 : 0);
    }

    public static <T> int getSize(@Nullable Collection<T> collection) {
        return collection != null ? collection.size() : 0;
    }

    public static <T>int getSize(@Nullable T[] array) {
        return array != null ? array.length : 0;
    }

    public static int getSize(@Nullable boolean[] array) {
        return array != null ? array.length : 0;
    }

    public static int getSize(@Nullable byte[] array) {
        return array != null ? array.length : 0;
    }

    public static int getSize(@Nullable char[] array) {
        return array != null ? array.length : 0;
    }

    public static int getSize(@Nullable int[] array) {
        return array != null ? array.length : 0;
    }

    public static int getSize(@Nullable short[] array) {
        return array != null ? array.length : 0;
    }

    public static int getSize(@Nullable long[] array) {
        return array != null ? array.length : 0;
    }

    public static int getSize(@Nullable float[] array) {
        return array != null ? array.length : 0;
    }

    public static int getSize(@Nullable double[] array) {
        return array != null ? array.length : 0;
    }

	/*创建数据*/

    /**
     * 使用默认构造方法创建clz的实例填充到数组，并转化为List。
     * 具体实现请参阅{@link #newArray(Class, int)}。
     *
     * @param clz
     * @param size
     * @param <T>
     * @return
     */
    public static <T> List<T> newList(Class<T> clz, int size) {
        T[] array = newArray(clz, size);
        return Arrays.asList(array);
    }

    /**
     * 使用默认构造方法创建clz的实例并填充到长度为size的数组中。
     * 任何一个操作失败都将抛出异常使得程序崩溃。
     *
     * @param clz
     * @param size
     * @param <T>
     * @return
     */
    public static <T> T[] newArray(Class<T> clz, int size) {
        try {
            T[] array = (T[]) Array.newInstance(clz, size);
            for (int i = 0, len = array != null ? array.length : 0; i < len; i++) {
                if (array[i] == null) {
                    array[i] = clz.newInstance();
                }
            }
            return array;
        } catch (Exception e) {
            LogUtil.e(e);
            throw new IllegalStateException();
        }
    }

}
