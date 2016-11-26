package core.mate.http;

import android.support.annotation.CallSuper;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.Type;

import core.mate.util.ClassUtil;
import core.mate.util.LogUtil;

/**
 * 用于封装服务器接口的基类。
 * 该基类会自动获取Result的类型并将服务器返回的字符串转型为其实例，
 * 因而当你实现该类的子类时请保留最后一个泛型参数为该Action的结果的数据类型。
 * <p>
 * 如果你想要下载文件的话最好使用{@link DownloadAction#}类。
 *
 * @param <Result>
 */
public abstract class ApiAction<Result> extends CoreAction<String, Result> {

    /*继承*/
    
    @Override
    public final Type getLoadType() {
        return String.class;
    }
    
    @Override
    public Type getResultType() {
        Type[] types = ClassUtil.getGenericParametersType(getClass());
        return types[types.length - 1];
    }
    
    /**
     * 将带有数据的字符串转化为真正需要用的数据类型，如将json的字符串转为object。
     * <br/>
     * <br/>
     * 根据{@link #isAutoConvertEnable()}的结果有以下情况：
     * <li/>为true，则默认使用{@link #onAutoConvertResult(String)}
     * 自动转化数据。如果自动转化失败则使用
     * {@link #onConvertComplexResult(String)}转化；
     * <li/>为false时，直接使用{@link #onConvertComplexResult(String)}
     * 转化。
     *
     * @param rawData
     * @return
     * @throws IllegalDataException
     */
    @CallSuper
    @Override
    protected Result onPrepareResult(String rawData) throws IllegalDataException {
        logRawData(rawData);
        
        rawData = onPrepareDataString(rawData);
        Result result = null;
        if (autoConvertEnable) {// 自动转化数据
            result = onAutoConvertResult(rawData);
        }
        if (result == null) {// 手动转换
            result = onConvertComplexResult(rawData);
        }
        
        if (getResultType() != Void.class && result == null) {// 我就干了，自动转换和手动转换都不行，简直呵呵哒
            throw new IllegalStateException("当Result类型不为Void且onAutoConvertResult不可用时，onConvertComplexResult方法不允许返回null。");
        }
        
        return result;
    }

    /*自动转换*/
    
    private boolean autoConvertEnable = true;
    
    public boolean isAutoConvertEnable() {
        return autoConvertEnable;
    }
    
    protected void setAutoConvertEnable(boolean autoConvertEnable) {
        this.autoConvertEnable = autoConvertEnable;
    }
    
    /**
     * 从文本中提取用于转化成对象的数据字符串，比如你的服务器返回的数据如下：
     * <b>{"code":OK,"msg":{"name":"小明","age":17} }</b>，其中msg标明的JSON字符串才是最终的数据字符串。
     * 如果数据不合法，比如数据中的code标明的是OK，设计上而言你应该抛出{@link IllegalDataException}。
     * 默认情况下直接返回。
     *
     * @param dataStr
     * @return
     * @throws IllegalDataException
     */
    protected String onPrepareDataString(String dataStr) throws IllegalDataException {
        return dataStr;
    }
    
    /**
     * 获取当前类中的泛型的具体类型，并自动将{@link #onPrepareDataString(String)}
     * 中返回的数据转为指定的Result对象。
     *
     * @param dataStr
     * @return
     */
    private Result onAutoConvertResult(String dataStr) {
        Type type = getResultType();
        logAutoConvertType(type);
        
        //如果适配的是Void类型的话，直接返回null
        if (type == Void.class) {
            return null;
        }
        
        try {
            if (type instanceof Class && type == String.class) {
                return (Result) dataStr;
            } else {
                return JSON.parseObject(dataStr, type);
            }
        } catch (Exception e) {
            LogUtil.e(e);
        }
        return null;
    }
    
    /**
     * 手动将数据的字符串转为对象。
     *
     * @param dataStr
     * @return
     * @throws IllegalDataException 当服务器的数据不合法时请抛出该异常。
     */
    protected Result onConvertComplexResult(String dataStr) throws IllegalDataException {
        return null;
    }

    /*日志*/
    
    private void logAutoConvertType(Type type) {
        if (isDevModeEnable()) {
            logDevMsg("Result泛型: ", type.toString());
        }
    }
    
    private void logRawData(String rawData) {
        if (isDevModeEnable()) {
            logDevMsg("原始数据：", rawData);
        }
    }
    
}
