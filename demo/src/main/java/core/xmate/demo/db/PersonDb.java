package core.xmate.demo.db;

import core.xmate.db.MateDb;

/**
 * @author DrkCore
 * @since 2017-05-20
 */
public class PersonDb extends MateDb {

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

    private PersonDb() {
        super(DB_NAME, DB_VERSION);
    }
}
