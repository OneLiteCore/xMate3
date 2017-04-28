package core.xmate.db.dao;

import core.xmate.db.DbManager;
import core.xmate.db.sqlite.SqlInfo;
import core.xmate.db.table.DbModel;

import core.xmate.db.AbsDao;

/**
 * 用于查找单个数据库模型的dao
 *
 * @author DrkCore
 * @since 2016年2月18日10:03:29
 */
public final class FindFirstDbModelDao extends AbsDao<DbModel> {


    private SqlInfo sqlInfo;

    public FindFirstDbModelDao setSql(SqlInfo sqlInfo) {
        this.sqlInfo = sqlInfo;
        return this;
    }

    public FindFirstDbModelDao setSql(String sql, Object... args) {
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
    public final DbModel access(DbManager db) throws Exception {
        return sqlInfo != null ? db.findDbModelFirst(sqlInfo) : null;
    }

    @Override
    public void clear() {
        super.clear();
        sqlInfo = null;
    }
}
