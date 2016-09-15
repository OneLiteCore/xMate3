package core.mate.db.dao;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.xutils.DbManager;
import org.xutils.db.sqlite.SqlInfo;

import core.mate.db.AbsDao;

public final class ExecNonQuerySqlDao extends AbsDao<Void> {

    private SqlInfo sqlInfo;

    public ExecNonQuerySqlDao setSql(SqlInfo sqlInfo) {
        this.sqlInfo = sqlInfo;
        return this;
    }

    public ExecNonQuerySqlDao setSql(String sql, @Nullable Object... args) {
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
    public Void access(@NonNull DbManager db) throws Exception {
        if (sqlInfo != null) {
            db.execNonQuery(sqlInfo);
        }
        return null;
    }

    @Override
    public void clear() {
        super.clear();
        sqlInfo = null;
    }
}
