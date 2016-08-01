package core.base.text;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * 字符过滤
 *
 * @author DrkCore
 * @since 2015年6月27日10:49:12
 */
public abstract class AbsCharFilter implements InputFilter {

    /**
     * 判断字符是否符合过滤规则
     *
     * @param ch
     * @return
     */
    public abstract boolean accept(char ch);

    public final boolean accept(CharSequence str) {
        for (int i = 0, len = str.length(); i < len; i++) {
            if (!accept(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public final boolean acceptIfAny(CharSequence str) {
        for (int i = 0, len = str.length(); i < len; i++) {
            if (accept(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

	/*字符过滤*/

    private boolean multiFilterAsOnceEnable = true;

    public final boolean isMultiFilterAsOnceEnable() {
        return multiFilterAsOnceEnable;
    }

    public final AbsCharFilter setMultiFilterAsOnceEnable(boolean multiFilterAsOnceEnable) {
        this.multiFilterAsOnceEnable = multiFilterAsOnceEnable;
        return this;
    }

    private TextBuilder clearStrBuilder;
    private TextBuilder filteredChars;

    /**
     * 过滤字符串
     *
     * @param charSequence
     * @return
     */
    public final CharSequence filter(CharSequence charSequence) {
        int len = charSequence.length();
        if (len == 0) {
            return "";
        }

        if (clearStrBuilder == null) {
            clearStrBuilder = new TextBuilder(len);
        }
        clearStrBuilder.clear();
        if (filteredChars == null) {
            filteredChars = new TextBuilder(multiFilterAsOnceEnable ? 16 : 1);
        }
        filteredChars.clear();

        char ch;
        boolean accept;
        boolean nextAccept = accept(charSequence.charAt(0));
        for (int i = 0; i < len; i++) {
            accept = nextAccept;
            nextAccept = i + 1 < len && accept(charSequence.charAt(i + 1));

            ch = charSequence.charAt(i);
            if (accept) {//通过检查，直接加入新的字符串
                clearStrBuilder.append(ch);
            } else if (multiFilterAsOnceEnable) {//开启连续过滤
                filteredChars.append(ch);
                if (nextAccept || i + 1 == len) {//下一个是可接受的，或者已经到结尾了
                    onCharFiltered(clearStrBuilder, filteredChars.toString());
                    filteredChars.clear();
                }
            } else {//没有连续过滤，直接走你
                filteredChars.append(ch);
                onCharFiltered(clearStrBuilder, filteredChars.toString());
                filteredChars.clear();
            }
        }
        return clearStrBuilder.toString();
    }

    /**
     * 当{@link #filter(CharSequence)}中抛弃未通过{@link #accept(char)}检测的字符时回调。
     * 你可以在该回调中补充允许的字符。
     * <br/>
     * 当通过{@link #isMultiFilterAsOnceEnable()}开启时连续过滤一组字符将只回调该方法一次。
     *
     * @param clearStrBuilder 合法的字符串的构造器
     * @param filteredChars   被过滤的字符
     */
    protected void onCharFiltered(TextBuilder clearStrBuilder, CharSequence filteredChars) {
    }

    @Override
    public final CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        CharSequence src = start == 0 && source.length() == end ? source : source.subSequence(start, end);
        if (accept(source)) {
            return source;
        }
        return filter(src);
    }

	/*统计字符*/

    public final int count(CharSequence charSequence) {
        return count(charSequence, 0, charSequence.length());
    }

    public final int count(CharSequence charSequence, int start, int end) {
        if (start < 0 || start > end || end > charSequence.length()) {
            throw new IndexOutOfBoundsException("start或end的值不合法。start = " + start + "，end = " + end + "，len = "
                    + charSequence.length());
        }

        int sum = 0;
        for (int i = start; i < end; i++) {
            if (accept(charSequence.charAt(i))) {
                sum++;
            }
        }
        return sum;
    }

    public final int count(char[] chars) {
        return count(chars, 0, chars.length);
    }

    public final int count(char[] chars, int start, int end) {
        int sum = 0;
        for (int i = start, len = chars.length; i < end && i < len; i++) {
            if (accept(chars[i])) {
                sum++;
            }
        }
        return sum;
    }

    public final int countUnaccepted(CharSequence charSequence) {
        return countUnaccepted(charSequence, 0, charSequence.length());
    }

    public final int countUnaccepted(CharSequence charSequence, int start, int end) {
        if (start < 0 || start > end || end > charSequence.length()) {
            throw new IndexOutOfBoundsException("start或end的值不合法。start = " + start + "，end = " + end + "，len = "
                    + charSequence.length());
        }

        int sum = 0;
        for (int i = start; i < end; i++) {
            if (!accept(charSequence.charAt(i))) {
                sum++;
            }
        }
        return sum;
    }

    public final int countUnaccepted(char[] chars) {
        return countUnaccepted(chars, 0, chars.length);
    }

    public final int countUnaccepted(char[] chars, int start, int end) {
        int sum = 0;
        for (int i = start, len = chars.length; i < end && i < len; i++) {
            if (!accept(chars[i])) {
                sum++;
            }
        }
        return sum;
    }

}
