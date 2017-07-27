package core.xmate;

import android.content.Context;

import core.xmate.db.DbManager;
import core.xmate.db.DbManagerImpl;

/**
 * @author DrkCore
 * @since 2017-07-08
 */
public class MateDb {

    private static Context context;

    public static void init(Context context) {
        MateDb.context = context.getApplicationContext();
    }

    public static DbManager getDb(DbManager.DaoConfig daoConfig) {
        if (context == null) {
            throw new IllegalStateException("MateDb数据库框架还未初始化");
        }
        return getDb(context, daoConfig);
    }

    public static DbManager getDb(Context context, DbManager.DaoConfig daoConfig) {
        return DbManagerImpl.getInstance(context, daoConfig);
    }

}
