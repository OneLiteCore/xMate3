package core.xmate.db.dao;

import core.xmate.db.DbException;
import core.xmate.db.DbManager;
import core.xmate.db.IDao;
import core.xmate.db.sqlite.SqlInfo;

public class NonQueryDao implements IDao<Void> {

    private final SqlInfo sqlInfo;

    public NonQueryDao(SqlInfo sqlInfo) {
        this.sqlInfo = sqlInfo;
    }

    @Override
    public Void access(DbManager db) throws DbException {
        db.execNonQuery(sqlInfo);
        return null;
    }
}
