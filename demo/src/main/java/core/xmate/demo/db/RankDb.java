package core.xmate.demo.db;

import java.util.ArrayList;
import java.util.List;

import core.xmate.db.AutoDb;
import core.xmate.db.DbException;
import core.xmate.db.DbManager;
import core.xmate.demo.db.rank.Level;
import core.xmate.demo.db.rank.Rank;

/**
 * @author DrkCore
 * @since 2017-05-20
 */
public class RankDb extends AutoDb {

    private static volatile RankDb instance = null;

    public static RankDb getInstance() {
        if (instance == null) {
            synchronized (RankDb.class) {
                if (instance == null) {
                    instance = new RankDb();
                }
            }
        }
        return instance;
    }

    private static final String DB_NAME = "rank.db";

    private RankDb() {
        super(DB_NAME, DB_VERSIONS, true);
    }

    private static final List<Class<? extends IVersion>> DB_VERSIONS = new ArrayList<>();

    static {
        DB_VERSIONS.add(VERSION_1.class);
        DB_VERSIONS.add(VERSION_2.class);
    }

    public static class VERSION_1 implements IVersion {
        @Override
        public void onUpgrade(DbManager db) throws DbException {
            db.createTableIfNotExist(Rank.class);
        }
    }

    public static class VERSION_2 implements IVersion {

        @Override
        public void onUpgrade(DbManager db) throws DbException {
            db.createTableIfNotExist(Level.class);
        }
    }

}
