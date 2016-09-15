package core.mate.db.dao;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.xutils.DbManager;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;

import java.util.List;

import core.mate.db.AbsDao;

/**
 * 用于查找数据库模型的dao
 *
 * @author DrkCore
 * @since 2016年2月17日11:39:23
 */
public final class FindDbModelDao extends AbsDao<List<DbModel>> {


    private SqlInfo sqlInfo;

    public FindDbModelDao setSqlInfo(SqlInfo sqlInfo) {
        this.sqlInfo = sqlInfo;
        return this;
    }

    public FindDbModelDao setSql(String sql, @Nullable Object... args) {
        if (sqlInfo == null) {
            sqlInfo = new SqlInfo();
        }
        sqlInfo.clear();
        sqlInfo.setSql(sql);
        if (args != null && args.length > 0) {
            sqlInfo.addBindArgs(args);
        }
        return this;
    }

    @Override
    public final List<DbModel> access(@NonNull DbManager db) throws Exception {
        return sqlInfo != null ? db.findDbModelAll(sqlInfo) : null;
    }

    @Override
    public void clear() {
        super.clear();
        sqlInfo = null;
    }
}
