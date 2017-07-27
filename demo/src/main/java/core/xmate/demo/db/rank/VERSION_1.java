package core.xmate.demo.db.rank;

import core.xmate.db.DbException;
import core.xmate.db.DbManager;
import core.xmate.db.IDao;

public class VERSION_1 implements IDao<Void> {

    @Override
    public Void access(DbManager db) throws DbException {
        db.createTableIfNotExist(Rank.class);
        return null;
    }
}