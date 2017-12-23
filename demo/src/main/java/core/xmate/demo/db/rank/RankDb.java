package core.xmate.demo.db.rank;

import java.util.ArrayList;
import java.util.List;

import core.xmate.db.AutoDb;
import core.xmate.db.DbException;
import core.xmate.db.DbManager;

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
        DB_VERSIONS.add(VERSION_3.class);
        DB_VERSIONS.add(VERSION_4.class);
    }

    public static class VERSION_1 implements IVersion {
        @Override
        public void onUpgrade(DbManager db) throws DbException {
            db.createTableIfNotExist(Rank.class);

            Rank rank = new Rank();
            rank.setName("王小明");
            db.save(rank);
        }
    }

    public static class VERSION_2 implements IVersion {

        @Override
        public void onUpgrade(DbManager db) throws DbException {
            db.createTableIfNotExist(Level.class);
        }
    }

    public static class VERSION_3 implements IVersion {

        @Override
        public void onUpgrade(DbManager db) throws DbException {
            db.createTableIfNotExist(RankV2.class);

            List<Rank> ranks = db.findAll(Rank.class);
            int size = ranks != null ? ranks.size() : 0;
            List<RankV2> rankV2s = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                Rank rank = ranks.get(i);
                RankV2 rankV2 = new RankV2();

                rankV2.setId(rank.getId());
                rankV2.setName(rank.getName());
                rankV2.setAge(123);

                rankV2s.add(rankV2);
            }
            db.save(rankV2s);
        }
    }

    public static class VERSION_4 implements IVersion {

        @Override
        public void onUpgrade(DbManager db) throws DbException {
            db.createTableIfNotExist(RankV3.class);

            List<RankV2> ranks = db.findAll(RankV2.class);
            int size = ranks != null ? ranks.size() : 0;
            List<RankV3> rankV3s = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                RankV2 rankV2 = ranks.get(i);
                RankV3 rankV3 = new RankV3();

                rankV3.setId(rankV2.getId());
                rankV3.setName(rankV2.getName());
                rankV3.setAge(rankV2.getAge());
                rankV3.setSex(true);

                rankV3s.add(rankV3);
            }
            db.save(rankV3s);
        }
    }

}
