package core.xmate;

import android.content.Context;

import core.xmate.db.DbManager;
import core.xmate.db.DbManagerImpl;

/**
 * @author DrkCore
 * @since 2017-07-08
 */
public class MateDb {

    public static DbManager getDb(Context context, DbManager.DaoConfig daoConfig) {
        return DbManagerImpl.getInstance(context, daoConfig);
    }

}
