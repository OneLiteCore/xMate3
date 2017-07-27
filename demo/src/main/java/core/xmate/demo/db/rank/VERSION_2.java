package core.xmate.demo.db.rank;

import core.xmate.db.DbException;
import core.xmate.db.DbManager;
import core.xmate.db.IDao;

public class VERSION_2 implements IDao<Void> {

    @Override
    public Void access(DbManager db) throws DbException {
        db.createTableIfNotExist(Level.class);
        return null;
    }
}