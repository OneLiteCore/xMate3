package core.base.text;

import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * @author DrkCore
 * @since 2016年5月11日23:20:34
 */
public final class ContainCharFilter extends AbsCharFilter {

    private final CharSequence containChars;

    public ContainCharFilter(@NonNull CharSequence containChars) {
        int len = containChars.length();
        if (len > 1) {
            StringBuilder builder = new StringBuilder(len);
            builder.append(containChars.charAt(0));//填入第一个字符，减少判断
            char c;
            for (int i = 1; i < len; i++) {
                c = containChars.charAt(i);
                for (int j = 0, buildLen = builder.length(); j < buildLen; j++) {
                    if (builder.charAt(j) == c) {//重复了，呵呵
                        break;
                    } else if (j == buildLen - 1) {//循环到最后并没有发现重复的字符串，走你
                        builder.append(c);
                        break;
                    }
                }
            }
            containChars = builder;
        }

        this.containChars = containChars;
    }

	/*常用字符*/

    public static final String SAMPLE_NUM = "1234567890";
    public static final String SAMPLE_CHN_NUM = "零一二三四五六七八九";
    public static final String SAMPLE_CHN_CAP_NUM = "壹贰叁肆伍陆柒捌玖";

    public static final String SAMPLE_LETTER = "qwertyuiopasdfghjklzxcvbnm";
    public static final String SAMPLE_LETTER_CAP = "QWERTYUIOPASDFGHJKLZXCVBNM";

	/*继承*/

    @Override
    public final boolean accept(char ch) {
        return TextUtils.indexOf(containChars, ch) >= 0;
    }

}
