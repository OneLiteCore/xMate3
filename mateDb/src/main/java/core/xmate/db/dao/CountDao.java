package core.xmate.db.dao;

import core.xmate.db.DbException;
import core.xmate.db.DbManager;
import core.xmate.db.IDao;

public class CountDao implements IDao<Long> {

    private final Class type;

    public CountDao(Class type) {
        this.type = type;
    }

    @Override
    public Long access(DbManager db) throws DbException {
        return db.selector(type).count();
    }

}
