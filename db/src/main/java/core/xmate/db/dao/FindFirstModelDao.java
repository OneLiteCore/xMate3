package core.xmate.db.dao;

import core.xmate.db.DbException;
import core.xmate.db.DbManager;
import core.xmate.db.IDao;

/**
 * 用于查找数据库模型的dao
 *
 * @author DrkCore
 * @since 2016年2月17日11:39:23
 */
public class FindFirstModelDao<Type> implements IDao<FindModelParams<Type>, Type> {

    @Override
    public Type access(DbManager db, FindModelParams<Type> params) throws DbException {
        return db.findModelFirst(params.type, params.sqlInfo);
    }
}
