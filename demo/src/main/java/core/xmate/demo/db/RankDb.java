package core.xmate.demo.db;

import core.xmate.db.AutoDb;
import core.xmate.demo.db.rank.Rank;
import core.xmate.demo.db.rank.VERSION_1;
import core.xmate.demo.db.rank.VERSION_2;

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
    private static final Class[] DB_VERSIONS = {VERSION_1.class, VERSION_2.class};

    private RankDb() {
        super(DB_NAME, DB_VERSIONS);
    }

}
