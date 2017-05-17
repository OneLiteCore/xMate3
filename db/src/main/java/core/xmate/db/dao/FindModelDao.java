package core.xmate.db.dao;

import java.util.List;

import core.xmate.db.DbException;
import core.xmate.db.DbManager;
import core.xmate.db.IDao;

/**
 * 用于查找数据库模型的dao
 *
 * @author DrkCore
 * @since 2016年2月17日11:39:23
 */
public class FindModelDao<Type> implements IDao<FindModelParams<Type>, List<Type>> {

    @Override
    public List<Type> access(DbManager db, FindModelParams<Type> params) throws DbException {
        return db.findModelAll(params.type, params.sqlInfo);
    }
}
