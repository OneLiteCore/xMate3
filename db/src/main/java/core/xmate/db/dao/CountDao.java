package core.xmate.db.dao;

import core.xmate.db.DbException;
import core.xmate.db.DbManager;
import core.xmate.db.IDao;

public class CountDao implements IDao<Class, Long> {

    @Override
    public Long access(DbManager db, Class aClass) throws DbException {
        return db.selector(aClass).count();
    }

}
