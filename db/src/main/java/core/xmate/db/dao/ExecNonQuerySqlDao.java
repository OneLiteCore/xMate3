package core.xmate.db.dao;

import core.xmate.db.DbManager;
import core.xmate.db.sqlite.SqlInfo;

import core.xmate.db.AbsDao;

public final class ExecNonQuerySqlDao extends AbsDao<Void> {

    private SqlInfo sqlInfo;

    public ExecNonQuerySqlDao setSql(SqlInfo sqlInfo) {
        this.sqlInfo = sqlInfo;
        return this;
    }

    public ExecNonQuerySqlDao setSql(String sql, Object... args) {
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
    public Void access(DbManager db) throws Exception {
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
