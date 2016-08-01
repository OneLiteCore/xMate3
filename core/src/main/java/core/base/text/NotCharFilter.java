package core.base.text;

import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * @author DrkCore
 * @since 2016年2月8日20:20:16
 */
public final class NotCharFilter extends AbsCharFilter {

    private final CharSequence notChars;

    public NotCharFilter(@NonNull CharSequence notChars) {
        int len = notChars.length();
        if (len > 1) {
            StringBuilder builder = new StringBuilder(len);
            builder.append(notChars.charAt(0));//填入第一个字符，减少判断
            char c;
            for (int i = 1; i < len; i++) {
                c = notChars.charAt(i);
                for (int j = 0, buildLen = builder.length(); j < buildLen; j++) {
                    if (builder.charAt(j) == c) {//重复了，呵呵
                        break;
                    } else if (j == buildLen - 1) {//循环到最后并没有发现重复的字符串，走你
                        builder.append(c);
                        break;
                    }
                }
            }
            notChars = builder;
        }

        this.notChars = notChars;
    }

	/*可过滤的字符串*/

    /**
     * 换行
     */
    public static final String SAMPLE_LF = "\n";
    /**
     * 回车
     */
    public static final String SAMPLE_CR = "\r";
    /**
     * 行符，包括换行和回车
     */
    public static final String SAMPLE_LINE_SEPARATOR = "\r\n";

    /**
     * 空字，包括换行，回车，空格，全角的空格，TAB
     */
    public static final String SAMPLE_EMPTY = "\t 　　    " + SAMPLE_LINE_SEPARATOR;
    public static final String SAMPLE_CHN_SIGN = "，。：；“”？！‘’、…—《》（）【】￥·~";
    public static final String SAMPLE_ENG_SIGN = ",.:;\"?!'`@#$%^&*()  []{}+-*/=_|\\";
    public static final String SAMPLE_SIGN_CHAR = SAMPLE_EMPTY + SAMPLE_CHN_SIGN + SAMPLE_ENG_SIGN;

	/*继承*/

    @Override
    public final boolean accept(char ch) {
        return TextUtils.indexOf(notChars, ch) < 0;
    }

}
