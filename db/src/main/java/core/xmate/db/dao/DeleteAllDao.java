package core.xmate.db.dao;

import core.xmate.db.DbException;
import core.xmate.db.DbManager;
import core.xmate.db.IDao;

/**
 * @author DrkCore
 * @since 2017-05-20
 */
public class DeleteAllDao<T> implements IDao<Void> {

    private final Class<T> type;

    public DeleteAllDao(Class<T> type) {
        this.type = type;
    }

    @Override
    public Void access(DbManager db) throws DbException {
        db.delete(type);
        return null;
    }
}
