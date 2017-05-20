package core.xmate.demo.db;

import core.xmate.db.DbException;
import core.xmate.db.DbManager;
import core.xmate.db.IDao;

/**
 * @author DrkCore
 * @since 2017-05-20
 */
public class DeleteDao implements IDao<Class,Void> {
    @Override
    public Void access(DbManager db, Class aClass) throws DbException {
        db.delete(aClass);
        return null;
    }
}
