package core.mate.db;

import android.support.annotation.NonNull;

import org.xutils.DbManager;

/**
 * 数据库访问对象
 *
 * @param <Result>
 * @author DrkCore
 * @since 2016年9月15日14:36:03
 */
public abstract class AbsDao<Result> {

    public abstract Result access(@NonNull DbManager db) throws Exception;

    /**
     * 清空对象的状态信息。但数据库访问对象作为缓存被取出的时候默认会回调该方法。
     */
    public void clear() {
    }
}
