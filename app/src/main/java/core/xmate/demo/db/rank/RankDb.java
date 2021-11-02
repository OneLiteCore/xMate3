package core.xmate.demo.db.rank;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import core.xmate.db.AutoDb;
import core.xmate.db.DbException;
import core.xmate.db.DbManager;
import core.xmate.db.sqlite.CursorIterator;
import core.xmate.util.LogUtil;

/**
 * @author DrkCore
 * @since 2017-05-20
 */
public class RankDb extends AutoDb {

    public static final String TAG = "RankDb";

    private static volatile RankDb instance = null;

    public static RankDb getInstance(Context context) {
        if (instance == null) {
            synchronized (RankDb.class) {
                if (instance == null) {
                    instance = new RankDb(context);
                }
            }
        }
        return instance;
    }

    private static final String DB_NAME = "rank.db";

    private RankDb(Context context) {
        super(context, DB_NAME, DB_VERSIONS);
    }

    private static final List<Class<? extends IVersion>> DB_VERSIONS = new ArrayList<>();

    static {
        DB_VERSIONS.add(VERSION_1.class);
        DB_VERSIONS.add(VERSION_2.class);
        DB_VERSIONS.add(VERSION_3.class);
        DB_VERSIONS.add(VERSION_4.class);
        DB_VERSIONS.add(VERSION_5.class);
    }

    public static class VERSION_1 implements IVersion {
        @Override
        public void onUpgrade(DbManager db) throws DbException {
            db.getTable(Rank.class).createTableIfNotExists();

            Rank rank = new Rank();
            rank.setName("王小明");
            db.save(rank);
        }
    }

    public static class VERSION_2 implements IVersion {

        @Override
        public void onUpgrade(DbManager db) throws DbException {
            db.getTable(Level.class).createTableIfNotExists();
        }
    }

    public static class VERSION_3 implements IVersion {

        @Override
        public void onUpgrade(DbManager db) throws DbException {
            db.getTable(RankV2.class).createTableIfNotExists();

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
            db.getTable(RankV3.class).createTableIfNotExists();

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

    public static class VERSION_5 implements IVersion {

        @Override
        public void onUpgrade(DbManager db) throws DbException {
            db.getTable(RankV4.class).createTableIfNotExists();

            CursorIterator<RankV3> iterator = db.selector(RankV3.class).queryIterator();
            RankV3 rankV3;
            while ((rankV3 = iterator.moveToNext()) != null) {
                RankV4 rankV4 = new RankV4();

                rankV4.setId(rankV3.getId());
                rankV4.setName(rankV3.getName());
                rankV4.setAge(rankV3.getAge());
                rankV4.setSex(rankV3.isSex());
                rankV4.setMajor("unknown");

                LogUtil.d(rankV4.toString());

                db.save(rankV4);
            }
            iterator.close();
        }
    }

}
