package core.mate.db.dao;

import android.support.annotation.NonNull;

import org.xutils.DbManager;

import core.mate.db.AbsDao;

/**
 * @author 陈锡强 178456643@qq.com
 * @since 2016年1月19日14:30:43
 */
public final class CountDao extends AbsDao<Long> {

    private Class table;

    public CountDao setTable(Class table) {
        this.table = table;
        return this;
    }

    @Override
    public Long access(@NonNull DbManager db) throws Exception {
        return table != null ? db.selector(table).count() : -1;
    }

    @Override
    public void clear() {
        super.clear();
        table = null;
    }
}
