package core.xmate.demo.db;

import core.xmate.db.AbsDb;
import core.xmate.db.DbException;
import core.xmate.db.DbManager;
import core.xmate.demo.App;
import core.xmate.x;

/**
 * @author DrkCore
 * @since 2017-05-20
 */
public class PersonDb extends AbsDb {

    private static volatile PersonDb instance = null;

    public static PersonDb getInstance() {
        if (instance == null) {
            synchronized (PersonDb.class) {
                if (instance == null) {
                    instance = new PersonDb();
                }
            }
        }
        return instance;
    }

    private static final String DB_NAME = "test.db";
    private static final int DB_VERSION = 1;

    public PersonDb() {
        super(DB_NAME, DB_VERSION);
    }

    @Override
    protected DbManager onCreate() throws DbException {
        DbManager mgr = x.getDb(App.getInstance(), this);
        ;
        mgr.createTableIfNotExist(Person.class);
        return mgr;
    }
}
