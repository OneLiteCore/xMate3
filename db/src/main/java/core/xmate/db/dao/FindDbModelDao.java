package core.xmate.db.dao;

import core.xmate.db.DbManager;
import core.xmate.db.sqlite.SqlInfo;
import core.xmate.db.table.DbModel;

import java.util.List;

import core.xmate.db.AbsDao;

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

    public FindDbModelDao setSql(String sql, Object... args) {
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
    public final List<DbModel> access(DbManager db) throws Exception {
        return sqlInfo != null ? db.findDbModelAll(sqlInfo) : null;
    }

    @Override
    public void clear() {
        super.clear();
        sqlInfo = null;
    }
}
