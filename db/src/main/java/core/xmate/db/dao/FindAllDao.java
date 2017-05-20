package core.xmate.db.dao;

import java.util.List;

import core.xmate.db.DbException;
import core.xmate.db.DbManager;
import core.xmate.db.IDao;

/**
 * @author DrkCore
 * @since 2017-05-20
 */
public class FindAllDao<T> implements IDao<List<T>> {

    private final Class<T> type;

    public FindAllDao(Class<T> type) {
        this.type = type;
    }

    @Override
    public List<T> access(DbManager db) throws DbException {
        return db.findAll(type);
    }
}
