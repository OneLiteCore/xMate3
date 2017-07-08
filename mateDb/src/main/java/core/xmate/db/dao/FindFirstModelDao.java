package core.xmate.db.dao;

import core.xmate.db.DbException;
import core.xmate.db.DbManager;
import core.xmate.db.IDao;
import core.xmate.db.sqlite.SqlInfo;

/**
 * 用于查找数据库模型的dao
 *
 * @author DrkCore
 * @since 2016年2月17日11:39:23
 */
public class FindFirstModelDao<T> implements IDao<T> {

    private final Class<T> type;
    private final SqlInfo sqlInfo;

    public FindFirstModelDao(Class<T> type, SqlInfo sqlInfo) {
        this.type = type;
        this.sqlInfo = sqlInfo;
    }

    @Override
    public T access(DbManager db) throws DbException {
        return db.findModelFirst(type, sqlInfo);
    }
}
