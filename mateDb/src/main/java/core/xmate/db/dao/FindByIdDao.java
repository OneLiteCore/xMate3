package core.xmate.db.dao;

import core.xmate.db.DbException;
import core.xmate.db.DbManager;
import core.xmate.db.IDao;

/**
 * @author DrkCore
 * @since 2017-05-20
 */
public class FindByIdDao<T> implements IDao<T> {

    public final Class<T> type;
    public final Object id;

    public FindByIdDao(Class<T> type, Object id) {
        this.type = type;
        this.id = id;
    }

    @Override
    public T access(DbManager db) throws DbException {
        return db.findById(type, id);
    }
}
