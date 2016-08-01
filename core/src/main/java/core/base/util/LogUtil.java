package core.base.util;

import android.util.Log;

import core.base.Core;
import core.base.text.TextBuilder;

/**
 * 用于简化日志输出的静态工具类
 *
 * @author DrkCore
 * @since 2015年12月8日11:21:55
 */
public class LogUtil {

    private LogUtil() {
    }

    private static boolean isLogEnable() {
        return Core.getInstance().isDevModeEnable();
    }

	/* 直接输出 */

    public static void v(String tag, Object msg) {
        if (isLogEnable()) {
            Log.v(tag != null ? tag : "MyVerbose", toString(msg));
        }
    }

    public static void i(String tag, Object msg) {
        if (isLogEnable()) {
            Log.i(tag != null ? tag : "MyInfo", toString(msg));
        }
    }

    public static void w(String tag, Object msg) {
        if (isLogEnable()) {
            Log.w(tag != null ? tag : "MyWarn", toString(msg));
        }
    }

    public static void e(String tag, Object msg) {
        if (isLogEnable()) {
            Log.e(tag != null ? tag : "MyError", toString(msg));
        }
    }

    public static void d(String tag, Object msg) {
        if (isLogEnable()) {
            Log.d(tag != null ? tag : "MyDebug", toString(msg));
        }
    }

	/*不带标签*/

    public static void v(Object msg) {
        v(null, toString(msg));
    }

    public static void i(Object msg) {
        i(null, toString(msg));
    }

    public static void w(Object msg) {
        w(null, toString(msg));
    }

    public static void e(Object msg) {
        e(null, toString(msg));
    }

    public static void d(Object msg) {
        d(null, toString(msg));
    }

	/*异常输出*/

    public static void e(Throwable e) {
        if (isLogEnable() && e != null) {
            e.printStackTrace();
        }
    }

	/* 按等级输出 */

    public enum LogLevel {

        VERBOSE, INFO, WARN, ERROR, DEBUG

    }

    public static void log(LogLevel level, Object msg) {
        log(level, null, msg);
    }

    public static void log(LogLevel level, String tag, Object msg) {
        if (isLogEnable()) {
            level = level != null ? level : LogLevel.DEBUG;
            switch (level) {
                case VERBOSE:
                    v(tag, msg);
                    break;

                case INFO:
                    i(tag, msg);
                    break;

                case WARN:
                    w(tag, msg);
                    break;

                case ERROR:
                    e(tag, msg);
                    break;

                case DEBUG:
                    d(tag, msg);
                    break;
            }
        }
    }

	/* 构建输出 */

    public static class Builder {

        private LogLevel level;
        private String tag;

        public Builder setLevel(LogLevel level) {
            this.level = level;
            return this;
        }

        public Builder setTag(String tag) {
            this.tag = tag;
            return this;
        }

		/*构造方法*/

        private Builder() {
        }

		/* 日志构建 */

        private TextBuilder textBuilder;

        private TextBuilder getTextBuilder() {
            if (textBuilder == null && isLogEnable()) {
                textBuilder = new TextBuilder();
            }
            return textBuilder;
        }

        public void clear() {
            if (getTextBuilder() != null) {
                textBuilder.clear();
            }
        }

        public Builder appendNewLine(Object... objs) {
            if (getTextBuilder() != null && objs != null) {
                textBuilder.appendNewLine(objs);
            }
            return this;
        }

        public Builder append(Object... objs) {
            if (getTextBuilder() != null && objs != null) {
                textBuilder.append(objs);
            }
            return this;
        }

        public void log() {
            if (getTextBuilder() != null) {
                LogUtil.log(level, tag, textBuilder.toString());
                textBuilder.clear();
            }
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

	/*数据处理*/

    /**
     * 将数据转化成字符串。
     *
     * @param msg
     * @return
     */
    private static String toString(Object msg) {
        if (msg == null) {
            return "null";
        } else if (CharSequence.class.isAssignableFrom(msg.getClass())) {
            return msg.toString();
        } else {
            //TODO 使用反射收集参数转成JSON输出
            // 这里只在测试环境下才会调用得到，不要太过在意效率
            return msg.toString();
        }
    }

}
