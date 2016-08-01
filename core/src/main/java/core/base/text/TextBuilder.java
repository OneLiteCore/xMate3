package core.base.text;

import android.text.TextUtils;

import core.base.util.EncodeUtil;

/**
 * 用于对字符串进行处理的工具类。
 * 注意，该类的所有方法都不是线程安全的，请不要在多个线程中同时操作该类的对象。
 *
 * @author DrkCore
 * @since 2015年11月9日16:23:16
 */
public final class TextBuilder {

    public static final int CAPACITY_DEFAULT = 128;

    private final StringBuilder stringBuilder;

    public TextBuilder() {
        this(CAPACITY_DEFAULT);
    }

    public TextBuilder(int capacity) {
        stringBuilder = new StringBuilder(capacity);
    }

	/* 配置 */

    private boolean emptyAsNullEnable;

    public boolean isEmptyAsNullEnable() {
        return emptyAsNullEnable;
    }

    public TextBuilder setEmptyAsNullEnable(boolean emptyAsNullEnable) {
        this.emptyAsNullEnable = emptyAsNullEnable;
        return this;
    }

	/* 拼接 */

    /**
     * 拼接多个字符串为一个独立的String
     *
     * @param objs
     * @return
     */
    public String buildString(Object... objs) {
        clear();
        return append(objs).toString();
    }

    public TextBuilder newLine() {
        stringBuilder.append('\n');
        return this;
    }

    public TextBuilder append(Object... objs) {
        int len = objs != null ? objs.length : 0;
        Object tmp;
        for (int i = 0; i < len; i++) {
            tmp = objs[i];
            if (tmp != null || !emptyAsNullEnable) {
                stringBuilder.append(tmp);
            }
        }
        return this;
    }

    /**
     * 在新的一行添加字符串。注意，如果字符串为空则不会添加换行符。
     *
     * @param objs
     * @return
     */
    public TextBuilder appendNewLine(Object... objs) {
        if (stringBuilder.length() > 0) {
            stringBuilder.append('\n');
        }
        return append(objs);
    }

    public TextBuilder appendMulti(Object obj, int count) {
        if (count < 0) {
            throw new IllegalArgumentException();
        }
        while (count-- > 0) {
            append(obj);
        }
        return this;
    }

    public TextBuilder appendHex(byte... bytes) {
        append(EncodeUtil.toHexString(bytes));
        return this;
    }

    /**
     * @param objs
     * @return
     */
    public TextBuilder insertStart(Object... objs) {
        return insert(0, objs);
    }

    /**
     * 插入字符串。idx为0表示插入开头。
     *
     * @param idx
     * @param objs
     * @return
     */
    public TextBuilder insert(int idx, Object... objs) {
        int len = objs != null ? objs.length : 0;
        if (len > 0) {
            Object tmp;
            StringBuilder tmpBuilder = new StringBuilder();
            for (int i = 0; i < len; i++) {
                tmp = objs[i];
                if (tmp != null || !emptyAsNullEnable) {
                    tmpBuilder.append(tmp);
                }
            }
            if (!TextUtils.isEmpty(tmpBuilder)) {
                stringBuilder.insert(idx, tmpBuilder);
            }
        }
        return this;
    }

	/*处理*/

    public TextBuilder reverse() {
        stringBuilder.reverse();
        return this;
    }

    public TextBuilder replace(CharSequence src, CharSequence dst) {
        return replace(new String[]{src.toString()}, new CharSequence[]{dst});
    }

    public TextBuilder replace(String[] src, CharSequence[] dst) {
        CharSequence newStr = TextUtils.replace(stringBuilder, src, dst);
        clear();
        stringBuilder.append(newStr);
        return this;
    }

    public TextBuilder confirmEnd(CharSequence end) {
        if (!endWith(end)) {
            append(end);
        }
        return this;
    }

    public TextBuilder confirmStart(CharSequence start) {
        if (!startWith(start)) {
            insertStart(start);
        }
        return this;
    }

    public TextBuilder deleteEnd(int len) {
        int strLen = length();
        stringBuilder.delete(strLen - len, strLen);
        return this;
    }

    public TextBuilder deleteStart(int len) {
        stringBuilder.delete(0, len);
        return this;
    }

    public TextBuilder removeEnd(CharSequence end) {
        if (endWith(end)) {
            deleteEnd(end.length());
        }
        return this;
    }

    public TextBuilder removeStart(CharSequence start) {
        if (startWith(start)) {
            deleteStart(start.length());
        }
        return this;
    }

    /**
     * 清空字符串
     */
    public TextBuilder clear() {
        if (stringBuilder.length() > 0) {
            stringBuilder.delete(0, stringBuilder.length());
        }
        return this;
    }

	/* 判断 */

    public boolean contains(CharSequence str) {
        return indexOf(str) != -1;
    }

    public int lastIndexOf(CharSequence str) {
        //TODO 优化查找算法
        return toString().lastIndexOf(str.toString());
    }

    public boolean startWith(CharSequence str) {
        return indexOf(str) == 0;
    }

    public boolean endWith(CharSequence str) {
        int len = length();
        int strLen = str.length();
        if (strLen == 0) {//空字符串直接是true的，
            return true;
        } else if (len == 0 || strLen > len) {//不解释，肯定false
            return false;
        }

        for (int i = 0; i < strLen; i++) {
            if (str.charAt(strLen - i - 1) != stringBuilder.charAt(len - i - 1)) {
                return false;
            }
        }
        return true;
    }

    public int indexOf(CharSequence str) {
        return indexOf(str, 0);
    }

    public int indexOf(CharSequence str, int start) {
        return TextUtils.indexOf(stringBuilder, str, start);
    }

    public int length() {
        return stringBuilder.length();
    }

    public boolean isEmpty() {
        return stringBuilder.length() == 0;
    }

	/*其他*/

    @Override
    public boolean equals(Object o) {
        return super.equals(o) || toString().equals(o);
    }

    @Override
    public String toString() {
        return stringBuilder.toString();
    }

    /**
     * 返回字符内容的{@link CharSequence}，
     * 注意，其内容可能会因为后续对该类的操作而改变。
     *
     * @return
     */
    public CharSequence asCharSequence() {
        return stringBuilder;
    }

    public TextBuilder setCharAt(int idx, char ch) {
        stringBuilder.setCharAt(idx, ch);
        return this;
    }

    public char charAt(int i) {
        return stringBuilder.charAt(i);
    }

    public String subString(int start) {
        return stringBuilder.substring(start);
    }

    public String subString(int start, int end) {
        return stringBuilder.substring(start, end);
    }

    public CharSequence subSequence(int start) {
        return stringBuilder.subSequence(start, stringBuilder.length());
    }

    public CharSequence subSequence(int start, int end) {
        return stringBuilder.subSequence(start, end);
    }
}
