package core.xmate;

import android.content.Context;

import core.xmate.db.DbManager;
import core.xmate.db.DbManagerImpl;

/**
 * @author DrkCore
 * @since 2017-05-15
 */
public class x {

    public static DbManager getDb(Context context, DbManager.DaoConfig daoConfig) {
        return DbManagerImpl.getInstance(context, daoConfig);
    }

}
