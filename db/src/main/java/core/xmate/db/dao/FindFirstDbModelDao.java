package core.xmate.db.dao;

import core.xmate.db.DbException;
import core.xmate.db.DbManager;
import core.xmate.db.IDao;
import core.xmate.db.sqlite.SqlInfo;
import core.xmate.db.table.DbModel;

/**
 * 用于查找单个数据库模型的dao
 *
 * @author DrkCore
 * @since 2016年2月18日10:03:29
 */
public class FindFirstDbModelDao implements IDao<SqlInfo, DbModel> {

    @Override
    public DbModel access(DbManager db, SqlInfo sqlInfo) throws DbException {
        return db.findDbModelFirst(sqlInfo);
    }
}
