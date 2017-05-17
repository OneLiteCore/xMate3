package core.xmate.db.dao;

import core.xmate.db.DbException;
import core.xmate.db.DbManager;
import core.xmate.db.IDao;
import core.xmate.db.sqlite.SqlInfo;

public class ExecNonQuerySqlDao implements IDao<SqlInfo, Void> {

    @Override
    public Void access(DbManager db, SqlInfo sqlInfo) throws DbException {
        db.execNonQuery(sqlInfo);
        return null;
    }
}
